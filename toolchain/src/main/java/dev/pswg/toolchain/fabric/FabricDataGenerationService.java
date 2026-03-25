package dev.pswg.toolchain.fabric;

import dev.pswg.toolchain.intellij.IntelliJDependencyResolver;
import dev.pswg.toolchain.intellij.IntelliJModuleNames;
import dev.pswg.toolchain.intellij.IntelliJPathMacros;
import dev.pswg.toolchain.intellij.IntelliJXmlWriter;
import dev.pswg.toolchain.mojang.MojangPaths;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleAggregationResolver;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.model.SourceSetNames;
import dev.pswg.toolchain.pswg.PswgRepositoryContext;
import dev.pswg.toolchain.source.SourceAttachmentResolver;
import dev.pswg.toolchain.runtime.LaunchEnvironment;
import dev.pswg.toolchain.runtime.LaunchIdentity;
import dev.pswg.toolchain.runtime.VanillaLaunchConfig;
import dev.pswg.toolchain.runtime.VanillaLaunchService;
import dev.pswg.toolchain.template.FileTemplateRenderer;
import dev.pswg.toolchain.template.XmlEscaper;
import dev.pswg.toolchain.util.HostPlatform;
import dev.pswg.toolchain.util.ToolchainLog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Generates module-scoped Fabric API datagen run configurations.
 *
 * <p>PSWG only uses client-side datagen. This service therefore prepares one client-derived Fabric
 * launch bundle that contains the full modeled PSWG runtime surface, then emits one IntelliJ run
 * configuration per datagen-capable module. Each generated run configuration pins both the Fabric
 * mod id and the checked-in output directory for that module so developers cannot accidentally run
 * `pswg_core` datagen against `pswg_gadgets` output roots.
 */
public final class FabricDataGenerationService
{
	/**
	 * The Fabric API system property that enables datagen mode.
	 */
	private static final String FABRIC_DATAGEN_FLAG = "-Dfabric-api.datagen";

	/**
	 * The Fabric API system property prefix for datagen output directories.
	 */
	private static final String FABRIC_DATAGEN_OUTPUT_DIR_PROPERTY = "-Dfabric-api.datagen.output-dir=";

	/**
	 * The Fabric API system property prefix for datagen mod selection.
	 */
	private static final String FABRIC_DATAGEN_MOD_ID_PROPERTY = "-Dfabric-api.datagen.modid=";

	/**
	 * The IntelliJ output directory segment used for compiled module outputs.
	 */
	private static final String INTELLIJ_OUTPUT_DIRECTORY = "out/production";

	/**
	 * The shared JSON serializer.
	 */
	private final ObjectMapper _mapper;

	/**
	 * The Fabric runtime resolver.
	 */
	private final FabricRuntimeResolver _runtimeResolver;

	/**
	 * Resolves IntelliJ compile classpaths so generated launches can exclude non-runtime entries.
	 */
	private final IntelliJDependencyResolver _ideaDependencyResolver;

	/**
	 * Resolves optional source archives for generated datagen launch libraries.
	 */
	private final SourceAttachmentResolver _sourceAttachmentResolver;

	/**
	 * Creates a new datagen service.
	 */
	public FabricDataGenerationService()
	{
		_mapper = new ObjectMapper();
		_runtimeResolver = new FabricRuntimeResolver();
		_ideaDependencyResolver = new IntelliJDependencyResolver();
		_sourceAttachmentResolver = new SourceAttachmentResolver();
	}

	/**
	 * The generated datagen configuration metadata for one module.
	 *
	 * @param moduleId the logical PSWG module identifier
	 * @param fabricModId the Fabric mod identifier selected for datagen
	 * @param outputDirectory the checked-in datagen output directory
	 * @param runConfigurationPath the generated IntelliJ run-configuration path
	 */
	public record DatagenConfiguration(
		String moduleId,
		String fabricModId,
		Path outputDirectory,
		Path runConfigurationPath
	)
	{
	}

	/**
	 * Prepares one or more module-scoped datagen run configurations.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether cached runtime artifacts should be refreshed
	 * @param requestedModuleId the optional requested datagen module identifier
	 * @param identity the client launch identity to embed
	 * @return the generated datagen configurations
	 * @throws IOException if generation fails
	 */
	public List<DatagenConfiguration> prepareRunConfigurations(
		String versionId,
		boolean refresh,
		String requestedModuleId,
		LaunchIdentity identity
	) throws IOException
	{
		PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
		List<ModuleSpec> targets = datagenTargets(repository.buildGraph(), requestedModuleId);
		String aggregateModuleId = repository.buildGraph().developmentModuleId();
		LaunchIdentity effectiveIdentity = LaunchEnvironment.CLIENT.effectiveIdentity(identity);
		VanillaLaunchConfig vanillaLaunch = new VanillaLaunchService().prepareRuntime(
			versionId,
			refresh,
			LaunchEnvironment.CLIENT,
			effectiveIdentity
		);
		FabricRuntimeArtifacts runtimeArtifacts = resolveFabricRuntimeArtifacts(repository.gradleProperties(), refresh);
		FabricModuleInjection moduleInjection = resolveModuleInjection(repository, aggregateModuleId, refresh);
		FabricDatagenPaths launchPaths = createLaunchPaths(
			repository.toolchainRoot(),
			repository.projectRoot(),
			repository.projectName(),
			versionId
		);

		prepareLaunchFiles(versionId, vanillaLaunch, moduleInjection.moduleRoots(), launchPaths);
		List<String> jvmArgs = buildFabricJvmArgs(vanillaLaunch, runtimeArtifacts, moduleInjection, launchPaths);
		VanillaLaunchConfig datagenLaunch = createDatagenLaunchConfig(
			versionId,
			jvmArgs,
			launchPaths.loggingConfigPath(),
			vanillaLaunch,
			launchPaths,
			effectiveIdentity
		);

		writeLaunchJson(launchPaths.serializedLaunchPath(), datagenLaunch);
		writeIdeaLaunchModule(repository, moduleInjection, datagenLaunch, launchPaths, refresh);
		return writeIdeaRunConfigurations(repository, datagenLaunch, launchPaths, targets);
	}

	/**
	 * Resolves the Fabric-side runtime artifacts implied by the tracked PSWG properties.
	 *
	 * @param gradleProperties the tracked repository Gradle properties
	 * @param refresh whether to revalidate cached runtime artifacts
	 * @return the resolved Fabric runtime artifacts
	 * @throws IOException if the runtime artifacts cannot be resolved
	 */
	private FabricRuntimeArtifacts resolveFabricRuntimeArtifacts(
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		String loaderVersion = gradleProperties.getProperty("loader_version");
		return _runtimeResolver.resolveRuntime(loaderVersion, refresh, LaunchEnvironment.CLIENT);
	}

	/**
	 * Resolves the aggregate PSWG module injection contract used by datagen launches.
	 *
	 * <p>Datagen needs the full modeled module surface so downstream generators can read the
	 * checked-in resources and generated output of upstream modules. The per-module output boundary
	 * is enforced by the generated run configuration's `fabric-api.datagen.modid` and output-dir
	 * properties, not by shrinking the runtime classpath to one module.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleId the aggregate module identifier used for datagen launches
	 * @param refresh whether to revalidate cached external runtime artifacts
	 * @return the resolved module injection contract
	 * @throws IOException if supporting external runtime dependencies cannot be resolved
	 */
	private FabricModuleInjection resolveModuleInjection(
		PswgRepositoryContext repository,
		String moduleId,
		boolean refresh
	) throws IOException
	{
		BuildGraph graph = repository.buildGraph();
		Path repoRoot = repository.projectRoot();
		String projectName = repository.projectName();
		ModuleSpec rootModule = ModuleAggregationResolver.requireModule(graph, moduleId);
		List<Path> moduleRoots = resolveOutputRoots(repoRoot, projectName, rootModule);
		List<Path> dependencyRoots = new ArrayList<>();
		List<MavenDependencySpec> runtimeDependencies = new ArrayList<>(rootModule.runtimeDependencies());

		for (ModuleSpec dependencyModule : ModuleAggregationResolver.aggregatedDependencies(graph, moduleId))
		{
			dependencyRoots.addAll(resolveOutputRoots(repoRoot, projectName, dependencyModule));
			runtimeDependencies.addAll(dependencyModule.runtimeDependencies());
		}

		List<Path> externalRuntimeArtifacts = resolveRuntimeDependencies(
			runtimeDependencies,
			repository.gradleProperties(),
			refresh
		);

		return new FabricModuleInjection(
			moduleId,
			moduleRoots,
			dependencyRoots.stream().distinct().toList(),
			externalRuntimeArtifacts
		);
	}

	/**
	 * Creates the standard path layout for the aggregate Fabric datagen launch bundle.
	 *
	 * @param toolchainRoot the toolchain project root
	 * @param repoRoot the tracked PSWG repository root
	 * @param projectName the IntelliJ project name
	 * @param versionId the Minecraft version identifier
	 * @return the derived launch paths
	 */
	private FabricDatagenPaths createLaunchPaths(
		Path toolchainRoot,
		Path repoRoot,
		String projectName,
		String versionId
	)
	{
		HostPlatform platform = HostPlatform.current();
		String platformId = platform.id();
		Path instanceRoot = toolchainRoot.resolve("work")
		                               .resolve("instances")
		                               .resolve("fabric-datagen")
		                               .resolve(platformId)
		                               .resolve(versionId);
		Path configDirectory = instanceRoot.resolve("config");

		return new FabricDatagenPaths(
			platformId,
			platform.displayName(),
			instanceRoot,
			configDirectory.resolve("launch.cfg"),
			configDirectory.resolve("log4j2-intellij.xml"),
			instanceRoot.resolve("launch.json"),
			repoRoot.resolve(".idea")
			        .resolve("modules")
			        .resolve("launch")
			        .resolve("fabric")
			        .resolve(IntelliJModuleNames.fabricDatagenLaunchModuleFileName(projectName, platformId)),
			repoRoot.resolve(".idea").resolve("runConfigurations")
		);
	}

	/**
	 * Prepares generated files that sit beside the aggregate Fabric datagen launch bundle.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param moduleRoots the injected grouped module roots that should be exposed as one logical mod
	 * @param launchPaths the generated launch path layout
	 * @throws IOException if any generated file cannot be written
	 */
	private void prepareLaunchFiles(
		String versionId,
		VanillaLaunchConfig vanillaLaunch,
		List<Path> moduleRoots,
		FabricDatagenPaths launchPaths
	) throws IOException
	{
		Files.createDirectories(launchPaths.configDirectory());
		Files.createDirectories(launchPaths.instanceRoot());
		Files.createDirectories(launchPaths.instanceRoot().resolve("game"));
		writeLoggingConfig(vanillaLaunch, launchPaths.loggingConfigPath());
		prepareFabricAssetIndex(versionId, vanillaLaunch.assetIndexId());
		writeDevLaunchConfig(
			versionId,
			vanillaLaunch,
			moduleRoots,
			launchPaths.launchConfigPath(),
			launchPaths.loggingConfigPath()
		);
	}

	/**
	 * Builds the final JVM argument list for the aggregate Fabric datagen launch.
	 *
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 * @param moduleInjection the resolved module injection contract
	 * @param launchPaths the generated launch path layout
	 * @return the final JVM argument list
	 */
	private List<String> buildFabricJvmArgs(
		VanillaLaunchConfig vanillaLaunch,
		FabricRuntimeArtifacts runtimeArtifacts,
		FabricModuleInjection moduleInjection,
		FabricDatagenPaths launchPaths
	)
	{
		List<String> jvmArgs = new ArrayList<>(vanillaLaunch.jvmArgs());
		List<Path> prependedClasspath = buildPrependedClasspath(moduleInjection, runtimeArtifacts);
		replaceClasspath(jvmArgs, prependedClasspath);
		replaceLoggingConfiguration(jvmArgs, launchPaths.loggingConfigPath());
		addFabricRuntimeProperties(jvmArgs, runtimeArtifacts, launchPaths);
		addHostCompatibilityFlags(jvmArgs);
		addMixinJavaAgent(jvmArgs, runtimeArtifacts);
		return jvmArgs;
	}

	/**
	 * Builds the classpath entries that must appear ahead of the vanilla runtime.
	 *
	 * @param moduleInjection the resolved module injection contract
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 * @return the ordered prepended classpath entries
	 */
	private List<Path> buildPrependedClasspath(
		FabricModuleInjection moduleInjection,
		FabricRuntimeArtifacts runtimeArtifacts
	)
	{
		List<Path> prependedClasspath = new ArrayList<>(moduleInjection.moduleRoots());
		prependedClasspath.addAll(moduleInjection.dependencyRoots());
		prependedClasspath.addAll(moduleInjection.externalRuntimeArtifacts());
		prependedClasspath.addAll(runtimeArtifacts.classpath());
		return prependedClasspath;
	}

	/**
	 * Replaces the logging configuration path inherited from the vanilla baseline.
	 *
	 * @param jvmArgs the JVM argument list
	 * @param loggingConfigPath the generated Fabric logging configuration path
	 */
	private void replaceLoggingConfiguration(List<String> jvmArgs, Path loggingConfigPath)
	{
		jvmArgs.removeIf(argument -> argument.startsWith("-Dlog4j.configurationFile="));
		jvmArgs.add("-Dlog4j.configurationFile=" + loggingConfigPath.toAbsolutePath());
		jvmArgs.add("-Dlog4j2.formatMsgNoLookups=true");
		jvmArgs.add("-Dfabric.log.disableAnsi=" + !FabricDevLaunchService.DEFAULT_ANSI_LOGGING_ENABLED);
	}

	/**
	 * Adds the core Fabric dev-launch runtime properties.
	 *
	 * @param jvmArgs the JVM argument list
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 * @param launchPaths the generated launch path layout
	 */
	private void addFabricRuntimeProperties(
		List<String> jvmArgs,
		FabricRuntimeArtifacts runtimeArtifacts,
		FabricDatagenPaths launchPaths
	)
	{
		jvmArgs.add("-Dfabric.dli.config=" + launchPaths.launchConfigPath().toAbsolutePath());
		jvmArgs.add("-Dfabric.dli.env=" + LaunchEnvironment.CLIENT.id());
		jvmArgs.add("-Dfabric.dli.main=" + runtimeArtifacts.runtimeMainClass());
		jvmArgs.add("-Dfabric.development=true");
	}

	/**
	 * Adds conservative JVM compatibility flags used by modern Minecraft launches.
	 *
	 * @param jvmArgs the JVM argument list
	 */
	private void addHostCompatibilityFlags(List<String> jvmArgs)
	{
		addIfMissing(jvmArgs, "--sun-misc-unsafe-memory-access=allow");
		addIfMissing(jvmArgs, "--enable-native-access=ALL-UNNAMED");
	}

	/**
	 * Adds the optional Mixin javaagent when the resolved runtime requires it.
	 *
	 * @param jvmArgs the JVM argument list
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 */
	private void addMixinJavaAgent(List<String> jvmArgs, FabricRuntimeArtifacts runtimeArtifacts)
	{
		if (runtimeArtifacts.mixinJavaAgentJar() != null)
		{
			addIfMissing(jvmArgs, "-javaagent:" + runtimeArtifacts.mixinJavaAgentJar().toAbsolutePath());
		}
	}

	/**
	 * Creates the final serialized aggregate Fabric datagen launch config.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param jvmArgs the resolved JVM arguments
	 * @param loggingConfigPath the generated logging configuration path
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param launchPaths the generated launch path layout
	 * @param identity the launch-time player identity
	 * @return the serialized launch configuration
	 */
	private VanillaLaunchConfig createDatagenLaunchConfig(
		String versionId,
		List<String> jvmArgs,
		Path loggingConfigPath,
		VanillaLaunchConfig vanillaLaunch,
		FabricDatagenPaths launchPaths,
		LaunchIdentity identity
	)
	{
		Path datagenWorkingDirectory = launchPaths.instanceRoot().resolve("game");

		return new VanillaLaunchConfig(
			versionId,
			FabricDevLaunchService.DEV_LAUNCH_MAIN_CLASS,
			vanillaLaunch.javaExecutable(),
			datagenWorkingDirectory,
			datagenWorkingDirectory,
			vanillaLaunch.assetsRoot(),
			vanillaLaunch.assetIndexId(),
			vanillaLaunch.nativesDirectory(),
			loggingConfigPath,
			vanillaLaunch.classpath(),
			jvmArgs,
			LaunchEnvironment.CLIENT.programArguments(identity)
		);
	}

	/**
	 * Creates the Fabric-style asset index alias expected by the dev launcher.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param assetIndexId the Mojang asset index identifier
	 * @throws IOException if the alias cannot be created
	 */
	private void prepareFabricAssetIndex(String versionId, String assetIndexId) throws IOException
	{
		MojangPaths paths = new MojangPaths();
		Path source = paths.assetIndexFile(assetIndexId);
		Path target = paths.assetIndexFile(versionId + "-" + assetIndexId);

		Files.createDirectories(target.getParent());
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Replaces the existing launch classpath with a Fabric-prepended classpath.
	 *
	 * @param jvmArgs the JVM argument list to update
	 * @param prependedClasspath the classpath entries to prepend
	 */
	private void replaceClasspath(List<String> jvmArgs, List<Path> prependedClasspath)
	{
		String separator = System.getProperty("path.separator");
		String prependedValue = prependedClasspath.stream()
		                                         .map(path -> path.toAbsolutePath().toString())
		                                         .reduce((left, right) -> left + separator + right)
		                                         .orElse("");

		for (int i = 0; i < jvmArgs.size() - 1; i++)
		{
			String arg = jvmArgs.get(i);

			if (!"-cp".equals(arg) && !"-classpath".equals(arg))
			{
				continue;
			}

			String existingClasspath = jvmArgs.get(i + 1);
			jvmArgs.set(i + 1, prependedValue + separator + existingClasspath);
			return;
		}

		if (!prependedValue.isBlank())
		{
			jvmArgs.add("-cp");
			jvmArgs.add(prependedValue);
		}
	}

	/**
	 * Adds a JVM argument if it is not already present.
	 *
	 * @param arguments the JVM argument list
	 * @param argument the argument to add
	 */
	private void addIfMissing(List<String> arguments, String argument)
	{
		if (!arguments.contains(argument))
		{
			arguments.add(argument);
		}
	}

	/**
	 * Writes the Fabric-style development launcher configuration file.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch configuration
	 * @param moduleRoots the injected grouped module roots that should be exposed as one logical mod
	 * @param outputPath the target launch configuration path
	 * @param loggingConfigPath the log4j configuration path
	 * @throws IOException if the file cannot be written
	 */
	private void writeDevLaunchConfig(
		String versionId,
		VanillaLaunchConfig vanillaLaunch,
		List<Path> moduleRoots,
		Path outputPath,
		Path loggingConfigPath
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("LOG4J_CONFIGURATION_FILE", loggingConfigPath.toAbsolutePath().toString());
		values.put("FABRIC_DEFAULT_MOD_DISTRIBUTION_NAMESPACE", FabricDevLaunchService.DEFAULT_MOD_DISTRIBUTION_NAMESPACE);
		values.put("FABRIC_DEFAULT_MIXIN_REMAP_TYPE", FabricDevLaunchService.DEFAULT_MIXIN_REMAP_TYPE);
		values.put("OPTIONAL_COMMON_PROPERTIES", optionalCommonProperties(moduleRoots));
		values.put("OPTIONAL_ENVIRONMENT_COMMON_PROPERTIES", environmentCommonProperties(versionId));
		values.put("OPTIONAL_ENVIRONMENT_PROPERTIES_SECTION", environmentPropertiesSection(versionId));
		values.put("ENVIRONMENT_ARGS_SECTION", environmentArgsSection(versionId, vanillaLaunch));
		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/fabric-dev-launch.cfg",
			values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Renders optional shared Fabric launcher properties for injected grouped module roots.
	 *
	 * @param moduleRoots the injected module classpath roots
	 * @return the rendered optional property lines, each ending in a newline
	 */
	private String optionalCommonProperties(List<Path> moduleRoots)
	{
		if (moduleRoots.size() < 2)
		{
			return "";
		}

		String joinedRoots = moduleRoots.stream()
		                               .map(path -> path.toAbsolutePath().toString())
		                               .reduce((left, right) -> left + System.getProperty("path.separator") + right)
		                               .orElse("");

		if (joinedRoots.isBlank())
		{
			return "";
		}

		return "\tfabric.classPathGroups=" + joinedRoots + "\n";
	}

	/**
	 * Renders Loom-style common game-jar properties for split source-set launches.
	 *
	 * @param versionId the Minecraft version identifier
	 * @return the rendered common property lines, each ending in a newline
	 */
	private String environmentCommonProperties(String versionId)
	{
		MojangPaths paths = new MojangPaths();
		Path commonGameJar = paths.extractedServerJarFile(versionId);
		return "\tfabric.gameJarPath=" + commonGameJar.toAbsolutePath() + "\n";
	}

	/**
	 * Renders client-specific Fabric dev-launch property sections.
	 *
	 * @param versionId the Minecraft version identifier
	 * @return the rendered section text
	 */
	private String environmentPropertiesSection(String versionId)
	{
		MojangPaths paths = new MojangPaths();
		Path clientGameJar = paths.clientJarFile(versionId);
		return "clientProperties\n"
			+ "\tfabric.gameJarPath.client=" + clientGameJar.toAbsolutePath() + "\n";
	}

	/**
	 * Renders client-specific Fabric dev-launch config sections.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @return the rendered client section text
	 */
	private String environmentArgsSection(
		String versionId,
		VanillaLaunchConfig vanillaLaunch
	)
	{
		return "clientArgs\n"
			+ "\t--assetIndex\n"
			+ "\t" + versionId + "-" + vanillaLaunch.assetIndexId() + "\n"
			+ "\t--assetsDir\n"
			+ "\t" + vanillaLaunch.assetsRoot().toAbsolutePath() + "\n";
	}

	/**
	 * Resolves a module from the authoritative PSWG graph.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the module identifier
	 * @return the resolved module specification
	 */
	/**
	 * Resolves the module targets that should receive datagen run configurations.
	 *
	 * @param graph the authoritative build graph
	 * @param requestedModuleId the optional requested module id
	 * @return the ordered datagen targets
	 */
	private List<ModuleSpec> datagenTargets(
		BuildGraph graph,
		String requestedModuleId
	)
	{
		List<ModuleSpec> supportedModules = graph.modules()
		                                     .stream()
		                                     .filter(this::supportsDatagen)
		                                     .toList();

		if (requestedModuleId == null || requestedModuleId.isBlank())
		{
			return supportedModules;
		}

		ModuleSpec requested = ModuleAggregationResolver.requireModule(graph, requestedModuleId);

		if (!supportsDatagen(requested))
		{
			throw new IllegalArgumentException("Module does not declare datagen support: " + requestedModuleId);
		}

		return List.of(requested);
	}

	/**
	 * Checks whether a module declares enough metadata to participate in datagen.
	 *
	 * @param module the candidate module
	 * @return {@code true} when datagen is supported
	 */
	private boolean supportsDatagen(ModuleSpec module)
	{
		return module.datagenOutput() != null
			&& module.fabricModJson() != null
			&& module.fabricModId() != null
			&& !module.fabricModId().isBlank();
	}

	/**
	 * Resolves declared module runtime Maven dependencies into concrete jars.
	 *
	 * @param runtimeDependencies the declared runtime dependencies
	 * @param gradleProperties the tracked repository Gradle properties
	 * @param refresh whether to revalidate cached dependency downloads
	 * @return the resolved runtime dependency jars
	 * @throws IOException if any dependency cannot be resolved
	 */
	private List<Path> resolveRuntimeDependencies(
		List<MavenDependencySpec> runtimeDependencies,
		Properties gradleProperties,
		boolean refresh
	) throws IOException
	{
		List<Path> artifacts = new ArrayList<>();

		for (MavenDependencySpec runtimeDependency : runtimeDependencies)
		{
			Path artifact = _runtimeResolver.resolveRuntimeDependency(runtimeDependency, gradleProperties, refresh);

			if (!artifacts.contains(artifact))
			{
				artifacts.add(artifact);
			}
		}

		return artifacts;
	}

	/**
	 * Derived path layout for the aggregate Fabric datagen launch bundle.
	 *
	 * @param platformId the current platform identifier
	 * @param platformDisplayName the current platform display name
	 * @param instanceRoot the Fabric datagen instance root
	 * @param launchConfigPath the generated DLI launch config path
	 * @param loggingConfigPath the generated log4j configuration path
	 * @param serializedLaunchPath the serialized launch JSON path
	 * @param ideaLaunchModulePath the generated IntelliJ launch module path
	 * @param ideaRunConfigurationsDirectory the generated IntelliJ run-configuration directory
	 */
	private record FabricDatagenPaths(
		String platformId,
		String platformDisplayName,
		Path instanceRoot,
		Path launchConfigPath,
		Path loggingConfigPath,
		Path serializedLaunchPath,
		Path ideaLaunchModulePath,
		Path ideaRunConfigurationsDirectory
	)
	{
		/**
		 * Gets the generated configuration directory.
		 *
		 * @return the configuration directory
		 */
		public Path configDirectory()
		{
			return launchConfigPath.getParent();
		}
	}

	/**
	 * The resolved module injection contract for the aggregate datagen launch.
	 *
	 * @param moduleId the injected aggregate module identifier
	 * @param moduleRoots the roots that form the injected grouped mod
	 * @param dependencyRoots the plain classpath roots for modeled module dependencies
	 * @param externalRuntimeArtifacts the external runtime artifacts required by the injected module
	 */
	private record FabricModuleInjection(
		String moduleId,
		List<Path> moduleRoots,
		List<Path> dependencyRoots,
		List<Path> externalRuntimeArtifacts
	)
	{
	}

	/**
	 * Resolves the IntelliJ output roots for an injected module.
	 *
	 * @param projectRoot the tracked repository root
	 * @param module the module specification
	 * @return the ordered output roots
	 */
	private List<Path> resolveOutputRoots(
		Path projectRoot,
		String projectName,
		ModuleSpec module
	)
	{
		List<Path> roots = new ArrayList<>();
		roots.addAll(resolveSourceSetOutputRoots(projectRoot, projectName, module, SourceSetNames.MAIN));
		roots.addAll(resolveSourceSetOutputRoots(projectRoot, projectName, module, SourceSetNames.CLIENT));
		return roots.stream()
		            .distinct()
		            .toList();
	}

	/**
	 * Resolves the IntelliJ output root for a single module source set.
	 *
	 * @param projectRoot the tracked repository root
	 * @param module the module specification
	 * @param sourceSetName the source set name
	 * @return the ordered output roots
	 */
	private List<Path> resolveSourceSetOutputRoots(
		Path projectRoot,
		String projectName,
		ModuleSpec module,
		String sourceSetName
	)
	{
		String moduleName = IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName);
		Path intellijOutput = projectRoot.resolve(INTELLIJ_OUTPUT_DIRECTORY).resolve(moduleName);

		if (Files.isDirectory(intellijOutput))
		{
			ToolchainLog.info("datagen", "Using IntelliJ output for " + module.id() + "." + sourceSetName + ": " + intellijOutput);
			return List.of(intellijOutput);
		}

		return List.of();
	}

	/**
	 * Writes the Loom-style IntelliJ log4j configuration for the datagen launch bundle.
	 *
	 * @param vanillaLaunch the prepared vanilla launch configuration
	 * @param outputPath the target configuration path
	 * @throws IOException if the file cannot be written
	 */
	private void writeLoggingConfig(VanillaLaunchConfig vanillaLaunch, Path outputPath) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("LATEST_LOG", xmlPath(vanillaLaunch.gameDirectory().resolve("logs").resolve("latest.log")));
		values.put("ARCHIVED_LOGS", xmlPath(vanillaLaunch.gameDirectory().resolve("logs").resolve("%d{yyyy-MM-dd}-%i.log.gz")));
		values.put("DEBUG_LOG", xmlPath(vanillaLaunch.gameDirectory().resolve("logs").resolve("debug.log")));
		values.put("DEBUG_ARCHIVED_LOGS", xmlPath(vanillaLaunch.gameDirectory().resolve("logs").resolve("debug-%i.log.gz")));
		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/log4j2-intellij.xml",
			values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Writes the serialized bootstrap launch JSON.
	 *
	 * @param outputPath the target launch JSON path
	 * @param config the launch configuration
	 * @throws IOException if the file cannot be written
	 */
	private void writeLaunchJson(Path outputPath, VanillaLaunchConfig config) throws IOException
	{
		Files.createDirectories(outputPath.getParent());
		_mapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), config);
	}

	/**
	 * Writes the generated IntelliJ launch module that owns the datagen runtime classpath.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param datagenLaunch the prepared datagen launch configuration
	 * @param launchPaths the generated launch path layout
	 * @throws IOException if the launch module metadata cannot be written
	 */
	private void writeIdeaLaunchModule(
		PswgRepositoryContext repository,
		FabricModuleInjection moduleInjection,
		VanillaLaunchConfig datagenLaunch,
		FabricDatagenPaths launchPaths,
		boolean refresh
	) throws IOException
	{
		writeIdeaLaunchLibraries(repository, launchPaths.platformId(), effectiveRuntimeClasspath(datagenLaunch), refresh);
		IntelliJXmlWriter.write(
			launchPaths.ideaLaunchModulePath(),
			createIdeaLaunchModuleDocument(repository, moduleInjection, datagenLaunch, launchPaths)
		);
		registerIdeaLaunchModule(repository, launchPaths);
	}

	/**
	 * Creates the IntelliJ launch module document used for aggregate datagen launches.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param datagenLaunch the prepared datagen launch configuration
	 * @param launchPaths the generated launch path layout
	 * @return the generated launch module document
	 */
	private Document createIdeaLaunchModuleDocument(
		PswgRepositoryContext repository,
		FabricModuleInjection moduleInjection,
		VanillaLaunchConfig datagenLaunch,
		FabricDatagenPaths launchPaths
	)
	{
		Document document = DocumentHelper.createDocument();
		Element module = document.addElement("module");
		module.addAttribute("version", "4");

		Element rootManager = module.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "true");
		rootManager.addElement("exclude-output");

		Element content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.fileUrl(repository.projectRoot(), launchPaths.instanceRoot()));
		content.addElement("excludeFolder")
		       .addAttribute("url", IntelliJPathMacros.fileUrl(repository.projectRoot(), launchPaths.instanceRoot()));

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");

		for (String moduleName : launchDependencyModuleNames(repository, moduleInjection))
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "module")
			           .addAttribute("module-name", moduleName)
			           .addAttribute("scope", "PROVIDED");
		}

		for (Path classpathEntry : effectiveRuntimeClasspath(datagenLaunch))
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", launchProjectLibraryName(launchPaths.platformId(), classpathEntry))
			           .addAttribute("level", "project");
		}

		return document;
	}

	/**
	 * Writes the dedicated IntelliJ project libraries used only by the aggregate datagen launch module.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param platformId the target platform identifier
	 * @param classpathEntries the prepared runtime classpath entries
	 * @throws IOException if the generated library metadata cannot be written
	 */
	private void writeIdeaLaunchLibraries(
		PswgRepositoryContext repository,
		String platformId,
		List<Path> classpathEntries,
		boolean refresh
	) throws IOException
	{
		Path librariesDirectory = repository.projectRoot().resolve(".idea").resolve("libraries");
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (Path classpathEntry : classpathEntries)
		{
			String fileName = launchProjectLibraryFileName(platformId, classpathEntry);
			expectedFileNames.add(fileName);
			Path sourceArchive = _sourceAttachmentResolver.resolveSourceArchive(classpathEntry, refresh);
			IntelliJXmlWriter.write(
				librariesDirectory.resolve(fileName),
				createIdeaLaunchLibraryDocument(repository.projectRoot(), platformId, classpathEntry, sourceArchive)
			);
		}

		deleteObsoleteLaunchLibraries(librariesDirectory, platformId, expectedFileNames);
	}

	/**
	 * Creates a generated project library document for a prepared runtime classpath entry.
	 *
	 * @param projectRoot the PSWG project root
	 * @param platformId the target platform identifier
	 * @param classpathEntry the prepared runtime classpath entry
	 * @return the launch library document
	 */
	private Document createIdeaLaunchLibraryDocument(
		Path projectRoot,
		String platformId,
		Path classpathEntry,
		Path sourceArchive
	)
	{
		Document document = DocumentHelper.createDocument();
		Element component = document.addElement("component");
		component.addAttribute("name", "libraryTable");
		Element library = component.addElement("library");
		library.addAttribute("name", launchProjectLibraryName(platformId, classpathEntry));
		Element classes = library.addElement("CLASSES");
		String url = Files.isDirectory(classpathEntry)
			? IntelliJPathMacros.fileUrl(projectRoot, classpathEntry)
			: IntelliJPathMacros.jarUrl(projectRoot, classpathEntry);
		classes.addElement("root").addAttribute("url", url);
		library.addElement("JAVADOC");
		Element sources = library.addElement("SOURCES");

		if (sourceArchive != null)
		{
			sources.addElement("root").addAttribute(
				"url",
				Files.isDirectory(sourceArchive)
					? IntelliJPathMacros.fileUrl(projectRoot, sourceArchive)
					: IntelliJPathMacros.jarUrl(projectRoot, sourceArchive)
			);
		}

		return document;
	}

	/**
	 * Deletes obsolete generated datagen launch-library metadata after regeneration.
	 *
	 * @param librariesDirectory the IntelliJ libraries directory
	 * @param platformId the target platform identifier
	 * @param expectedFileNames the expected generated launch-library files
	 * @throws IOException if stale launch-library files cannot be removed
	 */
	private void deleteObsoleteLaunchLibraries(
		Path librariesDirectory,
		String platformId,
		Set<String> expectedFileNames
	) throws IOException
	{
		if (!Files.isDirectory(librariesDirectory))
		{
			return;
		}

		String prefix = launchProjectLibraryPrefix(platformId);

		try (var entries = Files.list(librariesDirectory))
		{
			for (Path entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				String fileName = entry.getFileName().toString();

				if (!fileName.startsWith(prefix) || expectedFileNames.contains(fileName))
				{
					continue;
				}

				Files.deleteIfExists(entry);
			}
		}
	}

	/**
	 * Extracts the exact runtime classpath that the prepared launch would pass to Java.
	 *
	 * @param datagenLaunch the prepared launch configuration
	 * @return the ordered runtime classpath entries
	 */
	private List<Path> effectiveRuntimeClasspath(VanillaLaunchConfig datagenLaunch)
	{
		List<Path> entries = new ArrayList<>();
		String separator = System.getProperty("path.separator");

		for (int i = 0; i < datagenLaunch.jvmArgs().size() - 1; i++)
		{
			String argument = datagenLaunch.jvmArgs().get(i);

			if (!"-cp".equals(argument) && !"-classpath".equals(argument))
			{
				continue;
			}

			String classpath = datagenLaunch.jvmArgs().get(i + 1);

			for (String rawEntry : classpath.split(java.util.regex.Pattern.quote(separator)))
			{
				if (rawEntry.isBlank())
				{
					continue;
				}

				Path entry = Path.of(rawEntry);

				if (!entries.contains(entry))
				{
					entries.add(entry);
				}
			}
		}

		for (Path entry : datagenLaunch.classpath())
		{
			if (!entries.contains(entry))
			{
				entries.add(entry);
			}
		}

		return entries;
	}

	/**
	 * Resolves the IntelliJ module dependencies that should be built before the datagen launch runs.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleInjection the resolved injected module contract
	 * @return the ordered module dependency names
	 */
	private List<String> launchDependencyModuleNames(
		PswgRepositoryContext repository,
		FabricModuleInjection moduleInjection
	)
	{
		if (moduleInjection.moduleId() == null || moduleInjection.moduleId().isBlank())
		{
			return List.of();
		}

		BuildGraph graph = repository.buildGraph();
		List<String> moduleNames = new ArrayList<>();

		for (ModuleSpec module : launchDependencyModules(graph, moduleInjection.moduleId()))
		{
			moduleNames.add(IntelliJModuleNames.sourceSetModuleName(repository.projectName(), module.id(), SourceSetNames.MAIN));

			if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
			{
				moduleNames.add(IntelliJModuleNames.sourceSetModuleName(repository.projectName(), module.id(), SourceSetNames.CLIENT));
			}
		}

		return moduleNames;
	}

	/**
	 * Resolves the authoritative module closure that should be built before the datagen launch starts.
	 *
	 * @param graph the authoritative build graph
	 * @param rootModuleId the requested root module id
	 * @return the ordered module closure
	 */
	private List<ModuleSpec> launchDependencyModules(
		BuildGraph graph,
		String rootModuleId
	)
	{
		return ModuleAggregationResolver.aggregatedModules(graph, rootModuleId);
	}

	/**
	 * Builds the shared prefix used for generated datagen-only IntelliJ project libraries.
	 *
	 * @param platformId the target platform identifier
	 * @return the library prefix
	 */
	private String launchProjectLibraryPrefix(String platformId)
	{
		return "fabric-datagen-" + platformId + "-";
	}

	/**
	 * Builds the generated IntelliJ project-library name for a prepared runtime classpath entry.
	 *
	 * @param platformId the target platform identifier
	 * @param classpathEntry the prepared runtime classpath entry
	 * @return the launch library name
	 */
	private String launchProjectLibraryName(String platformId, Path classpathEntry)
	{
		return launchProjectLibraryPrefix(platformId) + classpathEntry.getFileName().toString();
	}

	/**
	 * Builds the generated IntelliJ project-library metadata file name for a prepared runtime classpath entry.
	 *
	 * @param platformId the target platform identifier
	 * @param classpathEntry the prepared runtime classpath entry
	 * @return the generated metadata file name
	 */
	private String launchProjectLibraryFileName(String platformId, Path classpathEntry)
	{
		return launchProjectLibraryName(platformId, classpathEntry)
			.replace(':', '_')
			.replace('/', '_')
			.replace('\\', '_')
			.replace(' ', '_')
			+ ".xml";
	}

	/**
	 * Ensures the generated datagen launch module is registered in the root IntelliJ project.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param launchPaths the generated launch path layout
	 * @throws IOException if the project registration cannot be updated
	 */
	private void registerIdeaLaunchModule(
		PswgRepositoryContext repository,
		FabricDatagenPaths launchPaths
	) throws IOException
	{
		Path modulesXmlPath = repository.projectRoot().resolve(".idea").resolve("modules.xml");
		Document document = readOrCreateProjectDocument(modulesXmlPath);
		Element project = document.getRootElement();
		Element component = firstOrCreate(project, "component", "name", "ProjectModuleManager");
		Element modules = firstOrCreate(component, "modules");
		String filePath = "$PROJECT_DIR$/.idea/modules/launch/fabric/"
			+ IntelliJModuleNames.fabricDatagenLaunchModuleFileName(repository.projectName(), launchPaths.platformId());

		removeRegisteredModule(modules, filePath);

		modules.addElement("module")
		       .addAttribute("fileurl", "file://" + filePath)
		       .addAttribute("filepath", filePath);
		IntelliJXmlWriter.write(modulesXmlPath, document);
	}

	/**
	 * Writes one IntelliJ Application run configuration per datagen-capable module.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param datagenLaunch the prepared aggregate datagen launch configuration
	 * @param launchPaths the generated launch path layout
	 * @param targets the datagen targets to emit
	 * @return the generated datagen configuration metadata
	 * @throws IOException if the run configurations cannot be written
	 */
	private List<DatagenConfiguration> writeIdeaRunConfigurations(
		PswgRepositoryContext repository,
		VanillaLaunchConfig datagenLaunch,
		FabricDatagenPaths launchPaths,
		List<ModuleSpec> targets
	) throws IOException
	{
		List<DatagenConfiguration> generated = new ArrayList<>();
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (ModuleSpec target : targets)
		{
			Path outputDirectory = repository.projectRoot().resolve(target.datagenOutput());
			Path runConfigurationPath = launchPaths.ideaRunConfigurationsDirectory()
			                                 .resolve(IntelliJModuleNames.fabricDatagenRunConfigurationFileName(target.id(), launchPaths.platformId()));
			expectedFileNames.add(runConfigurationPath.getFileName().toString());
			writeIdeaRunConfiguration(repository, runConfigurationPath, datagenLaunch, launchPaths, target, outputDirectory);
			generated.add(
				new DatagenConfiguration(
					target.id(),
					target.fabricModId(),
					outputDirectory,
					runConfigurationPath
				)
			);
		}

		deleteObsoleteRunConfigurations(launchPaths.ideaRunConfigurationsDirectory(), launchPaths.platformId(), expectedFileNames);
		return List.copyOf(generated);
	}

	/**
	 * Deletes obsolete generated datagen run configurations after regeneration.
	 *
	 * @param runConfigurationsDirectory the IntelliJ run-configuration directory
	 * @param platformId the target platform identifier
	 * @param expectedFileNames the expected generated run-configuration file names
	 * @throws IOException if stale run-configuration files cannot be removed
	 */
	private void deleteObsoleteRunConfigurations(
		Path runConfigurationsDirectory,
		String platformId,
		Set<String> expectedFileNames
	) throws IOException
	{
		if (!Files.isDirectory(runConfigurationsDirectory))
		{
			return;
		}

		String suffix = "_" + platformId.toUpperCase() + ".xml";

		try (var entries = Files.list(runConfigurationsDirectory))
		{
			for (Path entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				String fileName = entry.getFileName().toString();

				if (!fileName.startsWith("Fabric_Datagen_") || !fileName.endsWith(suffix) || expectedFileNames.contains(fileName))
				{
					continue;
				}

				Files.deleteIfExists(entry);
			}
		}
	}

	/**
	 * Writes the IntelliJ Application run configuration for one datagen target.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param outputPath the IntelliJ run configuration path
	 * @param datagenLaunch the prepared aggregate datagen launch configuration
	 * @param launchPaths the generated launch path layout
	 * @param target the target datagen module
	 * @param outputDirectory the checked-in datagen output directory
	 * @throws IOException if the run configuration cannot be written
	 */
	private void writeIdeaRunConfiguration(
		PswgRepositoryContext repository,
		Path outputPath,
		VanillaLaunchConfig datagenLaunch,
		FabricDatagenPaths launchPaths,
		ModuleSpec target,
		Path outputDirectory
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("CONFIG_NAME", "Fabric Datagen " + target.id() + " (" + launchPaths.platformDisplayName() + ")");
		values.put("MAIN_CLASS_NAME", FabricDevLaunchService.DEV_LAUNCH_MAIN_CLASS);
		values.put("MODULE_NAME", IntelliJModuleNames.fabricDatagenLaunchModuleName(repository.projectName(), launchPaths.platformId()));
		values.put("PROGRAM_PARAMETERS", renderIdeaArguments(datagenLaunch.gameArgs()));
		values.put("VM_PARAMETERS", renderIdeaArguments(datagenVmArguments(datagenLaunch.jvmArgs(), target, outputDirectory)));
		values.put("WORKING_DIRECTORY", xmlPath(datagenLaunch.workingDirectory()));
		values.put("CLASSPATH_MODIFICATIONS", "<classpathModifications/>");
		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/intellij-run-config.xml",
			values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Filters the prepared JVM arguments down to the values IntelliJ should pass directly, then
	 * appends the module-scoped Fabric API datagen properties.
	 *
	 * @param jvmArgs the prepared launch JVM arguments
	 * @param target the target datagen module
	 * @param outputDirectory the checked-in datagen output directory
	 * @return the IntelliJ VM arguments
	 */
	private List<String> datagenVmArguments(
		List<String> jvmArgs,
		ModuleSpec target,
		Path outputDirectory
	)
	{
		List<String> arguments = ideaVmArguments(jvmArgs);
		arguments.add(FABRIC_DATAGEN_FLAG);
		arguments.add(FABRIC_DATAGEN_OUTPUT_DIR_PROPERTY + outputDirectory.toAbsolutePath());
		arguments.add(FABRIC_DATAGEN_MOD_ID_PROPERTY + target.fabricModId());
		return arguments;
	}

	/**
	 * Filters the prepared JVM arguments down to the values IntelliJ should pass directly when it
	 * launches Fabric's dev-launch injector itself.
	 *
	 * @param jvmArgs the prepared launch JVM arguments
	 * @return the IntelliJ VM arguments
	 */
	private List<String> ideaVmArguments(List<String> jvmArgs)
	{
		List<String> arguments = new ArrayList<>();

		for (int i = 0; i < jvmArgs.size(); i++)
		{
			String argument = jvmArgs.get(i);

			if ("-cp".equals(argument) || "-classpath".equals(argument))
			{
				i++;
				continue;
			}

			if (argument.startsWith("-javaagent:"))
			{
				continue;
			}

			arguments.add(argument);
		}

		return arguments;
	}

	/**
	 * Renders IntelliJ command-line arguments for XML serialization.
	 *
	 * @param arguments the arguments to render
	 * @return the rendered argument string
	 */
	private String renderIdeaArguments(List<String> arguments)
	{
		return arguments.stream()
		                .map(this::quoteIdeaArgument)
		                .reduce((left, right) -> left + " " + right)
		                .orElse("");
	}

	/**
	 * Quotes an IntelliJ argument for XML serialization.
	 *
	 * @param argument the argument to quote
	 * @return the quoted argument
	 */
	private String quoteIdeaArgument(String argument)
	{
		return "&quot;" + XmlEscaper.escapeAttribute(argument) + "&quot;";
	}

	/**
	 * Reads an IntelliJ project XML document when present, or creates a new empty project document.
	 *
	 * @param path the IntelliJ project document path
	 * @return the parsed or synthesized document
	 * @throws IOException if the existing document cannot be parsed
	 */
	private Document readOrCreateProjectDocument(Path path) throws IOException
	{
		if (!Files.exists(path))
		{
			Document document = DocumentHelper.createDocument();
			document.addElement("project").addAttribute("version", "4");
			return document;
		}

		try
		{
			return DocumentHelper.parseText(Files.readString(path));
		}
		catch (Exception exception)
		{
			throw new IOException("Failed to parse IntelliJ document " + path, exception);
		}
	}

	/**
	 * Gets the first child element matching a name/attribute pair, creating it when absent.
	 *
	 * @param parent the parent element
	 * @param elementName the child element name
	 * @param attributeName the attribute name
	 * @param attributeValue the attribute value
	 * @return the existing or created element
	 */
	private Element firstOrCreate(Element parent, String elementName, String attributeName, String attributeValue)
	{
		for (Object candidate : parent.elements(elementName))
		{
			Element element = (Element) candidate;

			if (attributeValue.equals(element.attributeValue(attributeName)))
			{
				return element;
			}
		}

		Element created = parent.addElement(elementName);
		created.addAttribute(attributeName, attributeValue);
		return created;
	}

	/**
	 * Gets the first child element with the given name, creating it when absent.
	 *
	 * @param parent the parent element
	 * @param elementName the child element name
	 * @return the existing or created element
	 */
	private Element firstOrCreate(Element parent, String elementName)
	{
		for (Object candidate : parent.elements(elementName))
		{
			return (Element) candidate;
		}

		return parent.addElement(elementName);
	}

	/**
	 * Removes an IntelliJ module registration entry when it already exists.
	 *
	 * @param modules the `modules` element
	 * @param filePath the IntelliJ macro file path
	 */
	private void removeRegisteredModule(Element modules, String filePath)
	{
		List<Element> toRemove = new ArrayList<>();

		for (Object candidate : modules.elements("module"))
		{
			Element module = (Element) candidate;

			if (filePath.equals(module.attributeValue("filepath")))
			{
				toRemove.add(module);
			}
		}

		for (Element module : toRemove)
		{
			modules.remove(module);
		}
	}

	/**
	 * Escapes a filesystem path for safe use in XML attributes.
	 *
	 * @param path the path to escape
	 * @return the escaped path string
	 */
	private String xmlPath(Path path)
	{
		return XmlEscaper.escapePath(path);
	}
}
