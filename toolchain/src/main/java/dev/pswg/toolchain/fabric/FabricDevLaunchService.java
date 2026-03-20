package dev.pswg.toolchain.fabric;

import dev.pswg.toolchain.mojang.MojangPaths;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.MavenDependencySpec;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.pswg.definition.PswgBuildDefinition;
import dev.pswg.toolchain.runtime.LaunchIdentity;
import dev.pswg.toolchain.runtime.VanillaLaunchConfig;
import dev.pswg.toolchain.runtime.VanillaLaunchService;
import dev.pswg.toolchain.template.FileTemplateRenderer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Prepares a Fabric-style development launch configuration from toolchain metadata.
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
	 * The standard main source set name.
	 */
	public static final String MAIN_SOURCE_SET = "main";

	/**
	 * The standard client source set name.
	 */
	public static final String CLIENT_SOURCE_SET = "client";

	/**
	 * The shared JSON serializer.
	 */
	private final ObjectMapper _mapper;

	/**
	 * The Fabric runtime resolver.
	 */
	private final FabricRuntimeResolver _runtimeResolver;

	/**
	 * Creates a new Fabric dev launch service.
	 */
	public FabricDevLaunchService()
	{
		_mapper = new ObjectMapper();
		_runtimeResolver = new FabricRuntimeResolver();
	}

	/**
	 * Prepares a Fabric-style development launch bundle for the client environment.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force fresh runtime downloads
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
	 * @param refresh whether to force fresh runtime downloads
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
	 * @param refresh whether to force fresh runtime downloads
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
		Path projectRoot = Path.of("").toAbsolutePath().normalize();
		Path repoRoot = projectRoot.getParent();
		Properties gradleProperties = loadGradleProperties();
		VanillaLaunchConfig vanillaLaunch = new VanillaLaunchService().prepareClientRuntime(versionId, refresh, identity);
		FabricRuntimeArtifacts runtimeArtifacts = resolveFabricRuntimeArtifacts(gradleProperties, refresh);
		FabricModuleInjection moduleInjection = resolveModuleInjection(repoRoot, moduleId, gradleProperties, refresh);
		FabricLaunchPaths launchPaths = createLaunchPaths(projectRoot, versionId);

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
		writeIdeaRunConfiguration(
			projectRoot,
			launchPaths.ideaRunConfigurationPath(),
			launchPaths.serializedLaunchPath(),
			launchPaths.platformDisplayName()
		);
		return fabricLaunch;
	}

	/**
	 * Resolves the Fabric-side runtime artifacts implied by the tracked PSWG properties.
	 *
	 * @param gradleProperties the tracked repository Gradle properties
	 * @param refresh whether to force fresh runtime downloads
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
	 * @param repoRoot the tracked repository root
	 * @param moduleId the optional module identifier
	 * @param gradleProperties the tracked repository Gradle properties
	 * @param refresh whether to force fresh artifact downloads
	 * @return the resolved module injection contract
	 * @throws IOException if supporting external runtime dependencies cannot be resolved
	 */
	private FabricModuleInjection resolveModuleInjection(
		Path repoRoot,
		String moduleId,
		Properties gradleProperties,
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

		BuildGraph graph = new PswgBuildDefinition().define();
		ModuleSpec rootModule = requireModule(graph, moduleId);
		List<Path> moduleRoots = resolveOutputRoots(repoRoot, rootModule);
		List<Path> dependencyRoots = new ArrayList<>();
		List<MavenDependencySpec> runtimeDependencies = new ArrayList<>(rootModule.runtimeDependencies());
		Set<String> visited = new LinkedHashSet<>();

		for (String dependencyId : rootModule.dependencies())
		{
			collectDependencyRuntimeData(
				repoRoot,
				graph,
				dependencyId,
				visited,
				dependencyRoots,
				runtimeDependencies
			);
		}

		List<Path> externalRuntimeArtifacts = resolveRuntimeDependencies(runtimeDependencies, gradleProperties, refresh);

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
	 * @param projectRoot the toolchain project root
	 * @param versionId the Minecraft version identifier
	 * @return the derived launch paths
	 */
	private FabricLaunchPaths createLaunchPaths(Path projectRoot, String versionId)
	{
		String platformId = currentPlatformId();
		String platformDisplayName = currentPlatformDisplayName(platformId);
		Path instanceRoot = projectRoot.resolve("work")
		                               .resolve("instances")
		                               .resolve("fabric-client")
		                               .resolve(platformId)
		                               .resolve(versionId);
		Path configDirectory = instanceRoot.resolve("config");

		return new FabricLaunchPaths(
			platformId,
			platformDisplayName,
			instanceRoot,
			configDirectory.resolve("launch.cfg"),
			configDirectory.resolve("log4j2-intellij.xml"),
			instanceRoot.resolve("launch.json"),
			projectRoot.resolve(".idea")
			           .resolve("runConfigurations")
			           .resolve("Fabric_Client_" + platformId.toUpperCase(Locale.ROOT) + ".xml")
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
	 * Loads the tracked repository's Gradle properties.
	 *
	 * @return the parsed Gradle properties
	 * @throws IOException if the properties file cannot be read
	 */
	private Properties loadGradleProperties() throws IOException
	{
		Properties properties = new Properties();
		Path path = Path.of("").toAbsolutePath().normalize().getParent().resolve("gradle.properties");

		try (InputStream inputStream = Files.newInputStream(path))
		{
			properties.load(inputStream);
		}

		return properties;
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
		                               .reduce((left, right) -> left + File.pathSeparator + right)
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
		roots.addAll(resolveOutputRoots(projectRoot, module));
		runtimeDependencies.addAll(module.runtimeDependencies());

		for (String dependencyId : module.dependencies())
		{
			collectDependencyRuntimeData(
				projectRoot,
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
	 * @param refresh whether to force fresh downloads
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
	 * @param ideaRunConfigurationPath the generated IntelliJ run configuration path
	 */
	private record FabricLaunchPaths(
		String platformId,
		String platformDisplayName,
		Path instanceRoot,
		Path launchConfigPath,
		Path loggingConfigPath,
		Path serializedLaunchPath,
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
	 * Resolves usable output roots for a module, preferring IntelliJ outputs when available.
	 *
	 * @param projectRoot the tracked repository root
	 * @param module the module specification
	 * @return the ordered output roots
	 */
	private List<Path> resolveOutputRoots(Path projectRoot, ModuleSpec module)
	{
		List<Path> roots = new ArrayList<>();
		roots.addAll(resolveSourceSetOutputRoots(projectRoot, module, MAIN_SOURCE_SET));
		roots.addAll(resolveSourceSetOutputRoots(projectRoot, module, CLIENT_SOURCE_SET));

		return roots.stream()
		            .distinct()
		            .sorted(Comparator.comparing(Path::toString))
		            .toList();
	}

	/**
	 * Resolves usable output roots for a single module source set.
	 *
	 * @param projectRoot the tracked repository root
	 * @param module the module specification
	 * @param sourceSetName the source set name
	 * @return the ordered output roots
	 */
	private List<Path> resolveSourceSetOutputRoots(Path projectRoot, ModuleSpec module, String sourceSetName)
	{
		// IntelliJ compiles module outputs into a single directory per source set module.
		String moduleName = readProjectName(projectRoot) + ".projects." + module.id() + "." + sourceSetName;
		Path intellijOutput = projectRoot.resolve(INTELLIJ_OUTPUT_DIRECTORY).resolve(moduleName);

		if (Files.isDirectory(intellijOutput))
		{
			return List.of(intellijOutput);
		}

		Path moduleRoot = projectRoot.resolve(module.paths().root());
		List<Path> roots = new ArrayList<>();
		Path gradleClasses = moduleRoot.resolve("build").resolve("classes").resolve("java").resolve(sourceSetName);
		Path gradleResources = moduleRoot.resolve("build").resolve("resources").resolve(sourceSetName);

		if (Files.isDirectory(gradleClasses))
		{
			roots.add(gradleClasses);
		}

		if (Files.isDirectory(gradleResources))
		{
			roots.add(gradleResources);
		}

		return roots;
	}

	/**
	 * Reads the IntelliJ project name used in generated module output directories.
	 *
	 * @param projectRoot the IntelliJ project root
	 * @return the project name
	 */
	private String readProjectName(Path projectRoot)
	{
		Path projectNameFile = projectRoot.resolve(".idea").resolve(".name");

		try
		{
			if (Files.exists(projectNameFile))
			{
				String value = Files.readString(projectNameFile).trim();

				if (!value.isBlank())
				{
					return value;
				}
			}
		}
		catch (IOException ignored)
		{
		}

		return projectRoot.getFileName().toString();
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
	 * Writes the IntelliJ Application run configuration for the Fabric client launch bundle.
	 *
	 * @param projectRoot the toolchain project root
	 * @param outputPath the IntelliJ run configuration path
	 * @param launchJsonPath the serialized launch JSON path
	 * @param platformDisplayName the current platform display name
	 * @throws IOException if the run configuration cannot be written
	 */
	private void writeIdeaRunConfiguration(
		Path projectRoot,
		Path outputPath,
		Path launchJsonPath,
		String platformDisplayName
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("CONFIG_NAME", "Fabric Client (" + platformDisplayName + ")");
		values.put("MODULE_NAME", readIntelliJModuleName(projectRoot));
		values.put(
			"PROGRAM_PARAMETERS",
			"&quot;$PROJECT_DIR$/"
				+ projectRoot.relativize(launchJsonPath.toAbsolutePath().normalize()).toString().replace('\\', '/')
				+ "&quot;"
		);
		String rendered = FileTemplateRenderer.render(
			"dev/pswg/toolchain/templates/intellij-run-config.xml",
			values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Resolves the IntelliJ module name for the standalone toolchain main source set.
	 *
	 * @param projectRoot the IntelliJ project root
	 * @return the IntelliJ module name
	 * @throws IOException if project metadata cannot be read
	 */
	private String readIntelliJModuleName(Path projectRoot) throws IOException
	{
		Path projectNameFile = projectRoot.resolve(".idea").resolve(".name");

		if (Files.exists(projectNameFile))
		{
			return Files.readString(projectNameFile).trim() + ".main";
		}

		return "pswg-toolchain.main";
	}

	/**
	 * Resolves the current runtime platform identifier.
	 *
	 * @return the current platform identifier
	 */
	private String currentPlatformId()
	{
		String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

		if (osName.contains("win"))
		{
			return "windows";
		}

		if (osName.contains("mac"))
		{
			return "macos";
		}

		if (osName.contains("linux"))
		{
			return "linux";
		}

		return "unknown";
	}

	/**
	 * Resolves a platform display name from the platform identifier.
	 *
	 * @param platformId the platform identifier
	 * @return the platform display name
	 */
	private String currentPlatformDisplayName(String platformId)
	{
		return switch (platformId)
		{
			case "windows" -> "Windows";
			case "macos" -> "macOS";
			case "linux" -> "Linux";
			default -> "Unknown";
		};
	}

	/**
	 * Escapes a filesystem path for safe use in XML attributes.
	 *
	 * @param path the path to escape
	 * @return the escaped path string
	 */
	private String xmlPath(Path path)
	{
		return path.toAbsolutePath().toString().replace('\\', '/').replace("&", "&amp;").replace("\"", "&quot;");
	}
}
