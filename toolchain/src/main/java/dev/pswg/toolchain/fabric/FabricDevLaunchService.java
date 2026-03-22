package dev.pswg.toolchain.fabric;

import dev.pswg.toolchain.intellij.IntelliJDependencyResolver;
import dev.pswg.toolchain.intellij.IntelliJPathMacros;
import dev.pswg.toolchain.intellij.IntelliJModuleNames;
import dev.pswg.toolchain.intellij.IntelliJXmlWriter;
import dev.pswg.toolchain.mojang.MojangPaths;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.model.SourceSetNames;
import dev.pswg.toolchain.pswg.PswgRepositoryContext;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Prepares a Fabric-style development launch configuration from toolchain metadata.
 *
 * <p>This service layers Fabric's dev-launch bootstrap on top of the vanilla runtime bundle built
 * by {@link VanillaLaunchService}. Keeping the vanilla and Fabric steps separate makes it easier to
 * debug launcher parity issues without having to mentally untangle Mojang runtime resolution from
 * Fabric's additional classpath and `launch.cfg` conventions.
 */
public final class FabricDevLaunchService
{
	/**
	 * The Fabric dev launch injector entrypoint.
	 */
	public static final String DEV_LAUNCH_MAIN_CLASS = "net.fabricmc.devlaunchinjector.Main";

	/**
	 * The default namespace used for development-time mod distribution.
	 */
	public static final String DEFAULT_MOD_DISTRIBUTION_NAMESPACE = "official";

	/**
	 * The default Mixin remap mode for the current dev runtime model.
	 */
	public static final String DEFAULT_MIXIN_REMAP_TYPE = "static";

	/**
	 * Whether ANSI logging is enabled in generated development launches.
	 */
	public static final boolean DEFAULT_ANSI_LOGGING_ENABLED = true;

	/**
	 * The IntelliJ output directory segment used for compiled module outputs.
	 */
	public static final String INTELLIJ_OUTPUT_DIRECTORY = "out/production";

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
	 * Creates a new Fabric dev launch service.
	 */
	public FabricDevLaunchService()
	{
		_mapper = new ObjectMapper();
		_runtimeResolver = new FabricRuntimeResolver();
		_ideaDependencyResolver = new IntelliJDependencyResolver();
	}

	/**
	 * Prepares a Fabric-style development launch bundle for the client environment.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to revalidate cached runtime artifacts before launch preparation
	 * @return the prepared launch configuration
	 * @throws IOException if generation fails
	 */
	public VanillaLaunchConfig prepareClientLaunch(String versionId, boolean refresh) throws IOException
	{
		return prepareClientLaunch(versionId, refresh, null, LaunchIdentity.defaults());
	}

	/**
	 * Prepares a Fabric-style development launch bundle using the default development identity.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to revalidate cached runtime artifacts before launch preparation
	 * @param moduleId the optional PSWG module identifier to inject
	 * @return the prepared launch configuration
	 * @throws IOException if generation fails
	 */
	public VanillaLaunchConfig prepareClientLaunch(String versionId, boolean refresh, String moduleId) throws IOException
	{
		return prepareClientLaunch(versionId, refresh, moduleId, LaunchIdentity.defaults());
	}

	/**
	 * Prepares a Fabric-style development launch bundle for the client environment.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to revalidate cached runtime artifacts before launch preparation
	 * @param moduleId the optional PSWG module identifier to inject
	 * @param identity the launch-time player identity
	 * @return the prepared launch configuration
	 * @throws IOException if generation fails
	 */
	public VanillaLaunchConfig prepareClientLaunch(
		String versionId,
		boolean refresh,
		String moduleId,
		LaunchIdentity identity
	) throws IOException
	{
		PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
		Path toolchainRoot = repository.toolchainRoot();
		Path repoRoot = repository.projectRoot();
		VanillaLaunchConfig vanillaLaunch = new VanillaLaunchService().prepareClientRuntime(versionId, refresh, identity);
		FabricRuntimeArtifacts runtimeArtifacts = resolveFabricRuntimeArtifacts(repository.gradleProperties(), refresh);
		FabricModuleInjection moduleInjection = resolveModuleInjection(repository, moduleId, refresh);
		FabricLaunchPaths launchPaths = createLaunchPaths(toolchainRoot, repoRoot, repository.projectName(), versionId);

		prepareLaunchFiles(versionId, vanillaLaunch, moduleInjection.moduleRoots(), launchPaths);
		List<String> jvmArgs = buildFabricJvmArgs(
			vanillaLaunch,
			runtimeArtifacts,
			moduleInjection,
			launchPaths
		);
		VanillaLaunchConfig fabricLaunch = createFabricLaunchConfig(
			versionId,
			jvmArgs,
			launchPaths.loggingConfigPath(),
			vanillaLaunch,
			identity
		);

		writeLaunchJson(launchPaths.serializedLaunchPath(), fabricLaunch);
		writeIdeaLaunchModule(
			repository,
			moduleInjection,
			fabricLaunch,
			launchPaths
		);
		writeIdeaRunConfiguration(
			repository,
			launchPaths.ideaRunConfigurationPath(),
			fabricLaunch,
			moduleInjection,
			launchPaths,
			launchPaths.platformDisplayName(),
			refresh
		);
		return fabricLaunch;
	}

	/**
	 * Resolves the Fabric-side runtime artifacts implied by the tracked PSWG properties.
	 *
	 * @param gradleProperties the tracked repository Gradle properties
	 * @param refresh whether to revalidate cached runtime artifacts
	 * @return the resolved Fabric runtime artifacts
	 * @throws IOException if the runtime artifacts cannot be resolved
	 */
	private FabricRuntimeArtifacts resolveFabricRuntimeArtifacts(Properties gradleProperties, boolean refresh) throws IOException
	{
		String loaderVersion = gradleProperties.getProperty("loader_version");
		return _runtimeResolver.resolveClientRuntime(loaderVersion, refresh);
	}

	/**
	 * Resolves the optional module injection contract for a generated Fabric launch.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleId the optional module identifier
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
		if (moduleId == null || moduleId.isBlank())
		{
			return new FabricModuleInjection(
				null,
				List.of(),
				List.of(),
				List.of()
			);
		}

		BuildGraph graph = repository.buildGraph();
		Path repoRoot = repository.projectRoot();
		String projectName = repository.projectName();
		ModuleSpec rootModule = requireModule(graph, moduleId);
		List<Path> moduleRoots = resolveOutputRoots(repoRoot, projectName, rootModule);
		List<Path> dependencyRoots = new ArrayList<>();
		List<MavenDependencySpec> runtimeDependencies = new ArrayList<>(rootModule.runtimeDependencies());
		Set<String> visited = new LinkedHashSet<>();

		for (String dependencyId : rootModule.dependencies())
		{
			collectDependencyRuntimeData(
				repoRoot,
				projectName,
				graph,
				dependencyId,
				visited,
				dependencyRoots,
				runtimeDependencies
			);
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
	 * Creates the standard path layout for a generated Fabric client launch bundle.
	 *
	 * @param toolchainRoot the toolchain project root
	 * @param repoRoot the tracked PSWG repository root
	 * @param versionId the Minecraft version identifier
	 * @return the derived launch paths
	 */
	private FabricLaunchPaths createLaunchPaths(
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
		                               .resolve("fabric-client")
		                               .resolve(platformId)
		                               .resolve(versionId);
		Path configDirectory = instanceRoot.resolve("config");

		return new FabricLaunchPaths(
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
			        .resolve(IntelliJModuleNames.fabricLaunchModuleFileName(projectName, platformId)),
			repoRoot.resolve(".idea")
			        .resolve("runConfigurations")
			        .resolve(IntelliJModuleNames.fabricClientRunConfigurationFileName(platformId))
		);
	}

	/**
	 * Prepares generated files that sit beside a Fabric client launch bundle.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param moduleRoots the injected grouped module roots
	 * @param launchPaths the generated launch path layout
	 * @throws IOException if any generated file cannot be written
	 */
	private void prepareLaunchFiles(
		String versionId,
		VanillaLaunchConfig vanillaLaunch,
		List<Path> moduleRoots,
		FabricLaunchPaths launchPaths
	) throws IOException
	{
		Files.createDirectories(launchPaths.configDirectory());
		Files.createDirectories(launchPaths.instanceRoot());
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
	 * Builds the final JVM argument list for a generated Fabric client launch.
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
		FabricLaunchPaths launchPaths
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
		jvmArgs.add("-Dfabric.log.disableAnsi=" + !DEFAULT_ANSI_LOGGING_ENABLED);
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
		FabricLaunchPaths launchPaths
	)
	{
		jvmArgs.add("-Dfabric.dli.config=" + launchPaths.launchConfigPath().toAbsolutePath());
		jvmArgs.add("-Dfabric.dli.env=client");
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
	 * Creates the final serialized Fabric launch config.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param jvmArgs the resolved JVM arguments
	 * @param loggingConfigPath the generated logging configuration path
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param identity the launch-time player identity
	 * @return the serialized Fabric launch configuration
	 */
	private VanillaLaunchConfig createFabricLaunchConfig(
		String versionId,
		List<String> jvmArgs,
		Path loggingConfigPath,
		VanillaLaunchConfig vanillaLaunch,
		LaunchIdentity identity
	)
	{
		return new VanillaLaunchConfig(
			versionId,
			DEV_LAUNCH_MAIN_CLASS,
			vanillaLaunch.javaExecutable(),
			vanillaLaunch.workingDirectory(),
			vanillaLaunch.gameDirectory(),
			vanillaLaunch.assetsRoot(),
			vanillaLaunch.assetIndexId(),
			vanillaLaunch.nativesDirectory(),
			loggingConfigPath,
			vanillaLaunch.classpath(),
			jvmArgs,
			List.of("--username", identity.username(), "--uuid", identity.uuid())
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
		values.put("FABRIC_DEFAULT_MOD_DISTRIBUTION_NAMESPACE", DEFAULT_MOD_DISTRIBUTION_NAMESPACE);
		values.put("FABRIC_DEFAULT_MIXIN_REMAP_TYPE", DEFAULT_MIXIN_REMAP_TYPE);
		values.put("OPTIONAL_COMMON_PROPERTIES", optionalCommonProperties(moduleRoots));
		values.put("ASSET_INDEX", versionId + "-" + vanillaLaunch.assetIndexId());
		values.put("ASSETS_DIR", vanillaLaunch.assetsRoot().toAbsolutePath().toString());
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

		// Fabric groups split class/resource roots into a single logical mod using pathSeparator-delimited entries.
		return "\tfabric.classPathGroups=" + joinedRoots + "\n";
	}

	/**
	 * Resolves a module from the authoritative PSWG graph.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the module identifier
	 * @return the resolved module specification
	 */
	private ModuleSpec requireModule(BuildGraph graph, String moduleId)
	{
		return graph.modules()
		            .stream()
		            .filter(candidate -> moduleId.equals(candidate.id()))
		            .findFirst()
		            .orElseThrow(() -> new IllegalArgumentException("Unknown module id: " + moduleId));
	}

	/**
	 * Collects runtime roots and runtime Maven dependencies for a transitive modeled dependency.
	 *
	 * @param projectRoot the tracked repository root
	 * @param graph the build graph
	 * @param moduleId the dependency module identifier
	 * @param visited the visited dependency identifiers
	 * @param roots the accumulated runtime roots
	 * @param runtimeDependencies the accumulated runtime Maven dependencies
	 */
	private void collectDependencyRuntimeData(
		Path projectRoot,
		String projectName,
		BuildGraph graph,
		String moduleId,
		Set<String> visited,
		List<Path> roots,
		List<MavenDependencySpec> runtimeDependencies
	)
	{
		if (!visited.add(moduleId))
		{
			return;
		}

		ModuleSpec module = requireModule(graph, moduleId);
		roots.addAll(resolveOutputRoots(projectRoot, projectName, module));
		runtimeDependencies.addAll(module.runtimeDependencies());

		for (String dependencyId : module.dependencies())
		{
			collectDependencyRuntimeData(
				projectRoot,
				projectName,
				graph,
				dependencyId,
				visited,
				roots,
				runtimeDependencies
			);
		}
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
	 * Derived path layout for a generated Fabric client launch bundle.
	 *
	 * @param platformId the current platform identifier
	 * @param platformDisplayName the current platform display name
	 * @param instanceRoot the Fabric instance root
	 * @param launchConfigPath the generated DLI launch config path
	 * @param loggingConfigPath the generated log4j configuration path
	 * @param serializedLaunchPath the serialized launch JSON path
	 * @param ideaLaunchModulePath the generated IntelliJ launch module path
	 * @param ideaRunConfigurationPath the generated IntelliJ run configuration path
	 */
	private record FabricLaunchPaths(
		String platformId,
		String platformDisplayName,
		Path instanceRoot,
		Path launchConfigPath,
		Path loggingConfigPath,
		Path serializedLaunchPath,
		Path ideaLaunchModulePath,
		Path ideaRunConfigurationPath
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
	 * The resolved module injection contract for a generated Fabric launch.
	 *
	 * @param moduleId the optional injected module identifier
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
	private List<Path> resolveOutputRoots(Path projectRoot, String projectName, ModuleSpec module)
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
			ToolchainLog.info("fabric", "Using IntelliJ output for " + module.id() + "." + sourceSetName + ": " + intellijOutput);
			return List.of(intellijOutput);
		}

		ToolchainLog.info(
			"fabric",
			"No IntelliJ output found for " + module.id() + "." + sourceSetName + ". Build the generated Fabric Client configuration in IntelliJ first."
		);
		return List.of();
	}

	/**
	 * Writes the Loom-style IntelliJ log4j configuration for the Fabric launch bundle.
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
	 * Writes the generated IntelliJ launch module that owns the direct DLI runtime classpath.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param fabricLaunch the prepared Fabric launch configuration
	 * @param launchPaths the generated launch path layout
	 * @throws IOException if the launch module metadata cannot be written
	 */
	private void writeIdeaLaunchModule(
		PswgRepositoryContext repository,
		FabricModuleInjection moduleInjection,
		VanillaLaunchConfig fabricLaunch,
		FabricLaunchPaths launchPaths
	) throws IOException
	{
		writeIdeaLaunchLibraries(repository, launchPaths.platformId(), effectiveRuntimeClasspath(fabricLaunch));
		IntelliJXmlWriter.write(
			launchPaths.ideaLaunchModulePath(),
			createIdeaLaunchModuleDocument(repository, moduleInjection, fabricLaunch, launchPaths)
		);
		registerIdeaLaunchModule(repository, launchPaths);
	}

	/**
	 * Creates the IntelliJ launch module document used for direct Fabric DLI launches.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param fabricLaunch the prepared Fabric launch configuration
	 * @param launchPaths the generated launch path layout
	 * @return the generated launch module document
	 */
	private Document createIdeaLaunchModuleDocument(
		PswgRepositoryContext repository,
		FabricModuleInjection moduleInjection,
		VanillaLaunchConfig fabricLaunch,
		FabricLaunchPaths launchPaths
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

		for (Path classpathEntry : effectiveRuntimeClasspath(fabricLaunch))
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", launchProjectLibraryName(launchPaths.platformId(), classpathEntry))
			           .addAttribute("level", "project");
		}

		return document;
	}

	/**
	 * Writes the dedicated IntelliJ project libraries used only by a generated Fabric launch module.
	 *
	 * <p>These are intentionally separate from the normal IntelliJ compile libraries. The direct DLI
	 * launch must resolve the exact prepared runtime classpath, while the compile graph is free to
	 * point at transformed jars and exploded mod containers that would be invalid at runtime.
	 *
	 * @param projectRoot the PSWG project root
	 * @param platformId the target platform identifier
	 * @param classpathEntries the prepared runtime classpath entries
	 * @throws IOException if the generated library metadata cannot be written
	 */
	private void writeIdeaLaunchLibraries(
		PswgRepositoryContext repository,
		String platformId,
		List<Path> classpathEntries
	) throws IOException
	{
		Path librariesDirectory = repository.projectRoot().resolve(".idea").resolve("libraries");
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (Path classpathEntry : classpathEntries)
		{
			String fileName = launchProjectLibraryFileName(platformId, classpathEntry);
			expectedFileNames.add(fileName);
			IntelliJXmlWriter.write(
				librariesDirectory.resolve(fileName),
				createIdeaLaunchLibraryDocument(repository.projectRoot(), platformId, classpathEntry)
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
	private Document createIdeaLaunchLibraryDocument(Path projectRoot, String platformId, Path classpathEntry)
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
		library.addElement("SOURCES");
		return document;
	}

	/**
	 * Deletes obsolete generated launch-library metadata for a platform after regeneration.
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
	 * <p>The generated direct-DLI IntelliJ module must mirror the prepared `-cp` exactly. Using
	 * IntelliJ module dependencies here is wrong because it pulls in compile-time libraries from PSWG
	 * modules, including transformed Minecraft compile jars that must never appear on the real game
	 * runtime.
	 *
	 * @param fabricLaunch the prepared launch configuration
	 * @return the ordered runtime classpath entries
	 */
	private List<Path> effectiveRuntimeClasspath(VanillaLaunchConfig fabricLaunch)
	{
		List<Path> entries = new ArrayList<>();
		String separator = System.getProperty("path.separator");

		for (int i = 0; i < fabricLaunch.jvmArgs().size() - 1; i++)
		{
			String argument = fabricLaunch.jvmArgs().get(i);

			if (!"-cp".equals(argument) && !"-classpath".equals(argument))
			{
				continue;
			}

			String classpath = fabricLaunch.jvmArgs().get(i + 1);

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

			return entries;
		}

		for (Path entry : fabricLaunch.classpath())
		{
			if (!entries.contains(entry))
			{
				entries.add(entry);
			}
		}

		return entries;
	}

	/**
	 * Resolves the IntelliJ module dependencies that should be built before the generated launch module runs.
	 *
	 * <p>These are intentionally `PROVIDED` module edges in the launch module metadata so IntelliJ's
	 * `Make` step rebuilds PSWG outputs before launch, without letting those modules leak their
	 * compile-only classpaths into the actual direct-DLI runtime.
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
	 * Resolves the authoritative module closure that should be built before a generated launch starts.
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
		List<ModuleSpec> modules = new ArrayList<>();
		Set<String> visited = new LinkedHashSet<>();
		collectLaunchDependencyModules(graph, rootModuleId, visited, modules);
		return modules;
	}

	/**
	 * Collects the authoritative module closure for a generated launch.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the module identifier to collect
	 * @param visited the visited module ids
	 * @param modules the accumulated module closure
	 */
	private void collectLaunchDependencyModules(
		BuildGraph graph,
		String moduleId,
		Set<String> visited,
		List<ModuleSpec> modules
	)
	{
		if (!visited.add(moduleId))
		{
			return;
		}

		ModuleSpec module = requireModule(graph, moduleId);
		modules.add(module);

		for (String dependencyId : module.dependencies())
		{
			collectLaunchDependencyModules(graph, dependencyId, visited, modules);
		}
	}

	/**
	 * Builds the shared prefix used for generated launch-only IntelliJ project libraries.
	 *
	 * @param platformId the target platform identifier
	 * @return the library prefix
	 */
	private String launchProjectLibraryPrefix(String platformId)
	{
		return "fabric-launch-" + platformId + "-";
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
	 * Ensures the generated Fabric launch module is registered in the root IntelliJ project.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param launchPaths the generated launch path layout
	 * @throws IOException if the project registration cannot be updated
	 */
	private void registerIdeaLaunchModule(
		PswgRepositoryContext repository,
		FabricLaunchPaths launchPaths
	) throws IOException
	{
		Path modulesXmlPath = repository.projectRoot().resolve(".idea").resolve("modules.xml");
		Document document = readOrCreateProjectDocument(modulesXmlPath);
		Element project = document.getRootElement();
		Element component = firstOrCreate(project, "component", "name", "ProjectModuleManager");
		Element modules = firstOrCreate(component, "modules");
		String filePath = "$PROJECT_DIR$/.idea/modules/launch/fabric/"
			+ IntelliJModuleNames.fabricLaunchModuleFileName(repository.projectName(), launchPaths.platformId());

		removeRegisteredModule(modules, filePath);
		modules.addElement("module")
		       .addAttribute("fileurl", "file://" + filePath)
		       .addAttribute("filepath", filePath);
		IntelliJXmlWriter.write(modulesXmlPath, document);
	}

	/**
	 * Writes the IntelliJ Application run configuration for the Fabric client launch bundle.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param outputPath the IntelliJ run configuration path
	 * @param fabricLaunch the prepared Fabric launch configuration
	 * @param launchPaths the generated launch path layout
	 * @param platformDisplayName the current platform display name
	 * @throws IOException if the run configuration cannot be written
	 */
	private void writeIdeaRunConfiguration(
		PswgRepositoryContext repository,
		Path outputPath,
		VanillaLaunchConfig fabricLaunch,
		FabricModuleInjection moduleInjection,
		FabricLaunchPaths launchPaths,
		String platformDisplayName,
		boolean refresh
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("CONFIG_NAME", "Fabric Client (" + platformDisplayName + ")");
		values.put("MAIN_CLASS_NAME", DEV_LAUNCH_MAIN_CLASS);
		values.put("MODULE_NAME", IntelliJModuleNames.fabricLaunchModuleName(repository.projectName(), launchPaths.platformId()));
		values.put("PROGRAM_PARAMETERS", renderIdeaArguments(fabricLaunch.gameArgs()));
		values.put("VM_PARAMETERS", renderIdeaArguments(ideaVmArguments(fabricLaunch.jvmArgs())));
		values.put("WORKING_DIRECTORY", xmlPath(fabricLaunch.workingDirectory()));
		values.put(
			"CLASSPATH_MODIFICATIONS",
			renderIdeaClasspathModifications(
				launchClasspathExclusions(repository, moduleInjection, fabricLaunch, refresh)
			)
		);
		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/intellij-run-config.xml",
			values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
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
	 * Quotes an IntelliJ program argument for XML serialization.
	 *
	 * @param argument the argument to quote
	 * @return the quoted argument
	 */
	private String quoteIdeaArgument(String argument)
	{
		return "&quot;" + XmlEscaper.escapeAttribute(argument) + "&quot;";
	}

	/**
	 * Resolves the compile-time classpath entries IntelliJ should exclude from a generated launch.
	 *
	 * <p>The launch module intentionally keeps `PROVIDED` module edges so IntelliJ will rebuild the
	 * requested PSWG modules before launch. IntelliJ also threads those compile libraries into the
	 * Application runtime classpath, so this exclusion list trims the run config back down to the
	 * exact prepared Fabric runtime classpath.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param fabricLaunch the prepared Fabric launch configuration
	 * @param refresh whether dependency resolution should refresh cached artifacts
	 * @return the ordered classpath exclusions
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> launchClasspathExclusions(
		PswgRepositoryContext repository,
		FabricModuleInjection moduleInjection,
		VanillaLaunchConfig fabricLaunch,
		boolean refresh
	) throws IOException
	{
		if (moduleInjection.moduleId() == null || moduleInjection.moduleId().isBlank())
		{
			return List.of();
		}

		BuildGraph graph = repository.buildGraph();
		Set<Path> runtimeClasspath = new LinkedHashSet<>();

		for (Path entry : effectiveRuntimeClasspath(fabricLaunch))
		{
			runtimeClasspath.add(entry.toAbsolutePath().normalize());
		}

		Set<Path> exclusions = new LinkedHashSet<>();

		for (ModuleSpec module : launchDependencyModules(graph, moduleInjection.moduleId()))
		{
			for (Path dependency : _ideaDependencyResolver.resolveModuleLibraries(
				graph,
				repository.projectRoot(),
				repository.gradleProperties(),
				refresh,
				module,
				true
			))
			{
				Path normalized = dependency.toAbsolutePath().normalize();

				if (!runtimeClasspath.contains(normalized))
				{
					exclusions.add(normalized);
				}
			}
		}

		return List.copyOf(exclusions);
	}

	/**
	 * Renders the IntelliJ Application `classpathModifications` block.
	 *
	 * @param exclusions the ordered excluded classpath entries
	 * @return the serialized XML fragment
	 */
	private String renderIdeaClasspathModifications(List<Path> exclusions)
	{
		if (exclusions.isEmpty())
		{
			return "<classpathModifications/>";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<classpathModifications>");

		for (Path exclusion : exclusions)
		{
			builder.append("<entry exclude=\"true\" path=\"")
			       .append(XmlEscaper.escapeAttribute(exclusion.toString()))
			       .append("\"/>");
		}

		builder.append("</classpathModifications>");
		return builder.toString();
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
