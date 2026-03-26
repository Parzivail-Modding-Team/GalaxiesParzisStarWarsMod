package com.parzivail.toolchain.fabric;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parzivail.toolchain.intellij.IntelliJDependencyResolver;
import com.parzivail.toolchain.intellij.IntelliJModuleNames;
import com.parzivail.toolchain.intellij.IntelliJRunConfigurationSupport;
import com.parzivail.toolchain.model.MavenDependencySpec;
import com.parzivail.toolchain.model.ModuleAggregationResolver;
import com.parzivail.toolchain.model.ModuleSpec;
import com.parzivail.toolchain.model.SourceSetNames;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.runtime.LaunchIdentity;
import com.parzivail.toolchain.runtime.VanillaLaunchConfig;
import com.parzivail.toolchain.runtime.VanillaLaunchService;
import com.parzivail.toolchain.source.SourceAttachmentResolver;
import com.parzivail.toolchain.template.FileTemplateRenderer;
import com.parzivail.toolchain.template.TemplateXmlWriter;
import com.parzivail.toolchain.template.XmlEscaper;
import com.parzivail.toolchain.util.HostPlatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
	 * Resolves optional source archives for generated launch libraries.
	 */
	private final SourceAttachmentResolver _sourceAttachmentResolver;

	/**
	 * Creates a new Fabric dev launch service.
	 */
	public FabricDevLaunchService()
	{
		_mapper = new ObjectMapper();
		_runtimeResolver = new FabricRuntimeResolver();
		_ideaDependencyResolver = new IntelliJDependencyResolver();
		_sourceAttachmentResolver = new SourceAttachmentResolver();
	}

	/**
	 * Prepares a Fabric-style development launch bundle for one environment.
	 *
	 * @param versionId   the Minecraft version identifier
	 * @param refresh     whether to revalidate cached runtime artifacts before launch preparation
	 * @param moduleId    the optional module identifier to inject
	 * @param environment the launch environment
	 * @param identity    the launch-time player identity for client launches
	 *
	 * @return the prepared launch configuration
	 *
	 * @throws IOException if generation fails
	 */
	public VanillaLaunchConfig prepareLaunch(
			String versionId,
			boolean refresh,
			String moduleId,
			LaunchEnvironment environment,
			LaunchIdentity identity
	) throws IOException
	{
		var repository = RepositoryContext.load();
		var vanillaLaunch = prepareVanillaLaunch(versionId, refresh, environment, identity);
		var runtimeArtifacts = resolveFabricRuntimeArtifacts(repository.loaderVersion(), refresh, environment);
		var moduleInjection = resolveModuleInjection(repository, moduleId, refresh, environment);
		var launchPaths = createLaunchPaths(repository.projectName(), versionId, environment);

		prepareLaunchFiles(versionId, vanillaLaunch, moduleInjection.moduleRoots(), launchPaths, environment);
		var jvmArgs = buildFabricJvmArgs(
				vanillaLaunch,
				runtimeArtifacts,
				moduleInjection,
				launchPaths,
				environment
		);
		var fabricLaunch = createFabricLaunchConfig(
				versionId,
				jvmArgs,
				launchPaths.loggingConfigPath(),
				vanillaLaunch,
				environment,
				identity
		);

		FabricLaunchSupport.writeLaunchJson(_mapper, launchPaths.serializedLaunchPath(), fabricLaunch);
		writeIdeaLaunchModule(
				repository,
				moduleInjection,
				fabricLaunch,
				launchPaths,
				refresh
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
	 * Prepares the shared vanilla launch baseline for one Fabric environment.
	 *
	 * @param versionId   the Minecraft version identifier
	 * @param refresh     whether to refresh cached runtime artifacts
	 * @param environment the launch environment
	 * @param identity    the client launch identity
	 *
	 * @return the prepared vanilla launch config
	 *
	 * @throws IOException if preparation fails
	 */
	private VanillaLaunchConfig prepareVanillaLaunch(
			String versionId,
			boolean refresh,
			LaunchEnvironment environment,
			LaunchIdentity identity
	) throws IOException
	{
		var service = new VanillaLaunchService();
		return service.prepareRuntime(versionId, refresh, environment, identity);
	}

	/**
	 * Resolves the Fabric-side runtime artifacts implied by the tracked properties.
	 *
	 * @param loaderVersion the Fabric loader version
	 * @param refresh       whether to revalidate cached runtime artifacts
	 *
	 * @return the resolved Fabric runtime artifacts
	 *
	 * @throws IOException if the runtime artifacts cannot be resolved
	 */
	private FabricRuntimeArtifacts resolveFabricRuntimeArtifacts(
			String loaderVersion,
			boolean refresh,
			LaunchEnvironment environment
	) throws IOException
	{
		return _runtimeResolver.resolveRuntime(loaderVersion, refresh, environment);
	}

	/**
	 * Resolves the optional module injection contract for a generated Fabric launch.
	 *
	 * @param repository  the discovered repository context
	 * @param moduleId    the optional module identifier
	 * @param refresh     whether to revalidate cached external runtime artifacts
	 * @param environment the launch environment
	 *
	 * @return the resolved module injection contract
	 *
	 * @throws IOException if supporting external runtime dependencies cannot be resolved
	 */
	private FabricModuleInjection resolveModuleInjection(
			RepositoryContext repository,
			String moduleId,
			boolean refresh,
			LaunchEnvironment environment
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

		var graph = repository.buildGraph();
		var projectName = repository.projectName();
		var rootModule = ModuleAggregationResolver.requireModule(graph, moduleId);
		var moduleRoots = resolveOutputRoots(projectName, rootModule, environment);
		List<Path> dependencyRoots = new ArrayList<>();
		List<MavenDependencySpec> runtimeDependencies = new ArrayList<>(rootModule.runtimeDependencies());

		for (var dependencyModule : ModuleAggregationResolver.aggregatedDependencies(graph, moduleId))
		{
			dependencyRoots.addAll(resolveOutputRoots(projectName, dependencyModule, environment));
			runtimeDependencies.addAll(dependencyModule.runtimeDependencies());
		}

		var externalRuntimeArtifacts = resolveRuntimeDependencies(
				runtimeDependencies,
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
	 * Creates the standard path layout for a generated Fabric launch bundle.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the derived launch paths
	 */
	private FabricLaunchPaths createLaunchPaths(
			String projectName,
			String versionId,
			LaunchEnvironment environment
	)
	{
		var platform = HostPlatform.current();
		var platformId = platform.id();
		var instanceRoot = ToolchainPaths.getInstanceRoot(versionId, environment, platform);
		var configDirectory = instanceRoot.resolve(ToolchainPaths.INSTANCE_CONFIG_ROOT_NAME);

		return new FabricLaunchPaths(
				environment,
				platformId,
				platform.displayName(),
				instanceRoot,
				configDirectory.resolve("launch.cfg"),
				configDirectory.resolve("log4j2-intellij.xml"),
				instanceRoot.resolve("launch.json"),
				ToolchainPaths.INTELLIJ_FABRIC_LAUNCH_MODULE_DIRECTORY
						.resolve(IntelliJModuleNames.fabricLaunchModuleFileName(projectName, environment.id(), platformId)),
				ToolchainPaths.INTELLIJ_RUN_CONFIGS_DIRECTORY
						.resolve(IntelliJModuleNames.fabricRunConfigurationFileName(environment.id(), platformId))
		);
	}

	/**
	 * Prepares generated files that sit beside a Fabric launch bundle.
	 *
	 * @param versionId     the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param moduleRoots   the injected grouped module roots
	 * @param launchPaths   the generated launch path layout
	 *
	 * @throws IOException if any generated file cannot be written
	 */
	private void prepareLaunchFiles(
			String versionId,
			VanillaLaunchConfig vanillaLaunch,
			List<Path> moduleRoots,
			FabricLaunchPaths launchPaths,
			LaunchEnvironment environment
	) throws IOException
	{
		Files.createDirectories(launchPaths.configDirectory());
		Files.createDirectories(launchPaths.instanceRoot());
		FabricLaunchSupport.writeLoggingConfig(vanillaLaunch, launchPaths.loggingConfigPath());
		if (environment.isClient())
		{
			FabricLaunchSupport.prepareFabricAssetIndex(versionId, vanillaLaunch.assetIndexId());
		}
		writeDevLaunchConfig(
				versionId,
				vanillaLaunch,
				moduleRoots,
				launchPaths.launchConfigPath(),
				launchPaths.loggingConfigPath(),
				environment
		);
	}

	/**
	 * Builds the final JVM argument list for a generated Fabric launch.
	 *
	 * @param vanillaLaunch    the prepared vanilla launch baseline
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 * @param moduleInjection  the resolved module injection contract
	 * @param launchPaths      the generated launch path layout
	 *
	 * @return the final JVM argument list
	 */
	private List<String> buildFabricJvmArgs(
			VanillaLaunchConfig vanillaLaunch,
			FabricRuntimeArtifacts runtimeArtifacts,
			FabricModuleInjection moduleInjection,
			FabricLaunchPaths launchPaths,
			LaunchEnvironment environment
	)
	{
		List<String> jvmArgs = new ArrayList<>(vanillaLaunch.jvmArgs());
		var prependedClasspath = FabricLaunchSupport.buildPrependedClasspath(
				moduleInjection.moduleRoots(),
				moduleInjection.dependencyRoots(),
				moduleInjection.externalRuntimeArtifacts(),
				runtimeArtifacts.classpath()
		);
		FabricLaunchSupport.replaceClasspath(jvmArgs, prependedClasspath);
		FabricLaunchSupport.replaceLoggingConfiguration(jvmArgs, launchPaths.loggingConfigPath(), DEFAULT_ANSI_LOGGING_ENABLED);
		addFabricRuntimeProperties(jvmArgs, runtimeArtifacts, launchPaths, environment);
		FabricLaunchSupport.addHostCompatibilityFlags(jvmArgs);
		FabricLaunchSupport.addMixinJavaAgent(jvmArgs, runtimeArtifacts);
		return jvmArgs;
	}

	/**
	 * Adds the core Fabric dev-launch runtime properties.
	 *
	 * @param jvmArgs          the JVM argument list
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 * @param launchPaths      the generated launch path layout
	 */
	private void addFabricRuntimeProperties(
			List<String> jvmArgs,
			FabricRuntimeArtifacts runtimeArtifacts,
			FabricLaunchPaths launchPaths,
			LaunchEnvironment environment
	)
	{
		jvmArgs.add("-Dfabric.dli.config=" + launchPaths.launchConfigPath().toAbsolutePath());
		jvmArgs.add("-Dfabric.dli.env=" + environment.id());
		jvmArgs.add("-Dfabric.dli.main=" + runtimeArtifacts.runtimeMainClass());
		jvmArgs.add("-Dfabric.development=true");
	}

	/**
	 * Creates the final serialized Fabric launch config.
	 *
	 * @param versionId         the Minecraft version identifier
	 * @param jvmArgs           the resolved JVM arguments
	 * @param loggingConfigPath the generated logging configuration path
	 * @param vanillaLaunch     the prepared vanilla launch baseline
	 * @param identity          the launch-time player identity
	 *
	 * @return the serialized Fabric launch configuration
	 */
	private VanillaLaunchConfig createFabricLaunchConfig(
			String versionId,
			List<String> jvmArgs,
			Path loggingConfigPath,
			VanillaLaunchConfig vanillaLaunch,
			LaunchEnvironment environment,
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
				environment.programArguments(environment.effectiveIdentity(identity))
		);
	}

	/**
	 * Writes the Fabric-style development launcher configuration file.
	 *
	 * @param versionId         the Minecraft version identifier
	 * @param vanillaLaunch     the prepared vanilla launch configuration
	 * @param moduleRoots       the injected grouped module roots that should be exposed as one logical mod
	 * @param outputPath        the target launch configuration path
	 * @param loggingConfigPath the log4j configuration path
	 *
	 * @throws IOException if the file cannot be written
	 */
	private void writeDevLaunchConfig(
			String versionId,
			VanillaLaunchConfig vanillaLaunch,
			List<Path> moduleRoots,
			Path outputPath,
			Path loggingConfigPath,
			LaunchEnvironment environment
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("LOG4J_CONFIGURATION_FILE", loggingConfigPath.toAbsolutePath().toString());
		values.put("FABRIC_DEFAULT_MOD_DISTRIBUTION_NAMESPACE", DEFAULT_MOD_DISTRIBUTION_NAMESPACE);
		values.put("FABRIC_DEFAULT_MIXIN_REMAP_TYPE", DEFAULT_MIXIN_REMAP_TYPE);
		values.put("OPTIONAL_COMMON_PROPERTIES", FabricLaunchSupport.optionalCommonProperties(moduleRoots));
		values.put("OPTIONAL_ENVIRONMENT_COMMON_PROPERTIES", FabricLaunchSupport.environmentCommonProperties(versionId));
		values.put("OPTIONAL_ENVIRONMENT_PROPERTIES_SECTION", environmentPropertiesSection(versionId, environment));
		values.put("ENVIRONMENT_ARGS_SECTION", environmentArgsSection(versionId, vanillaLaunch, environment));
		var rendered = FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/fabric-dev-launch.cfg",
				values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Renders environment-specific Fabric dev-launch property sections.
	 *
	 * <p>Loom only emits a side-specific game-jar property for the client environment. Server
	 * launches rely exclusively on the shared `fabric.gameJarPath` common property.
	 *
	 * @param versionId   the Minecraft version identifier
	 * @param environment the launch environment
	 *
	 * @return the rendered section text
	 */
	private String environmentPropertiesSection(
			String versionId,
			LaunchEnvironment environment
	)
	{
		if (!environment.isClient())
		{
			return "";
		}

		return FabricLaunchSupport.clientPropertiesSection(versionId);
	}

	/**
	 * Renders environment-specific Fabric dev-launch config sections.
	 *
	 * @param versionId     the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param environment   the launch environment
	 *
	 * @return the rendered environment-specific section text
	 */
	private String environmentArgsSection(
			String versionId,
			VanillaLaunchConfig vanillaLaunch,
			LaunchEnvironment environment
	)
	{
		if (!environment.isClient())
		{
			return "";
		}

		return FabricLaunchSupport.clientArgsSection(versionId, vanillaLaunch);
	}

	/**
	 * Resolves declared module runtime Maven dependencies into concrete jars.
	 *
	 * @param runtimeDependencies the declared runtime dependencies
	 * @param refresh             whether to revalidate cached dependency downloads
	 *
	 * @return the resolved runtime dependency jars
	 *
	 * @throws IOException if any dependency cannot be resolved
	 */
	private List<Path> resolveRuntimeDependencies(
			List<MavenDependencySpec> runtimeDependencies,
			boolean refresh
	) throws IOException
	{
		return FabricLaunchSupport.resolveRuntimeDependencies(_runtimeResolver, runtimeDependencies, refresh);
	}

	/**
	 * Derived path layout for a generated Fabric launch bundle.
	 *
	 * @param platformId               the current platform identifier
	 * @param platformDisplayName      the current platform display name
	 * @param instanceRoot             the Fabric instance root
	 * @param launchConfigPath         the generated DLI launch config path
	 * @param loggingConfigPath        the generated log4j configuration path
	 * @param serializedLaunchPath     the serialized launch JSON path
	 * @param ideaLaunchModulePath     the generated IntelliJ launch module path
	 * @param ideaRunConfigurationPath the generated IntelliJ run configuration path
	 */
	private record FabricLaunchPaths(
			LaunchEnvironment environment,
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
	 * @param moduleId                 the optional injected module identifier
	 * @param moduleRoots              the roots that form the injected grouped mod
	 * @param dependencyRoots          the plain classpath roots for modeled module dependencies
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
	 * @param module the module specification
	 *
	 * @return the ordered output roots
	 */
	private List<Path> resolveOutputRoots(
			String projectName,
			ModuleSpec module,
			LaunchEnvironment environment
	)
	{
		return FabricModuleOutputResolver.resolveOutputRoots(
				projectName,
				module,
				environment.isClient(),
				"fabric",
				true
		);
	}

	/**
	 * Writes the generated IntelliJ launch module that owns the direct DLI runtime classpath.
	 *
	 * @param repository      the discovered repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param fabricLaunch    the prepared Fabric launch configuration
	 * @param launchPaths     the generated launch path layout
	 *
	 * @throws IOException if the launch module metadata cannot be written
	 */
	private void writeIdeaLaunchModule(
			RepositoryContext repository,
			FabricModuleInjection moduleInjection,
			VanillaLaunchConfig fabricLaunch,
			FabricLaunchPaths launchPaths,
			boolean refresh
	) throws IOException
	{
		var runtimeClasspath = IntelliJRunConfigurationSupport.effectiveRuntimeClasspath(fabricLaunch);
		var generatedPrefix = "fabric-launch-" + launchPaths.environment().id() + "-" + launchPaths.platformId() + "-";
		IntelliJRunConfigurationSupport.writeLaunchLibraries(
				generatedPrefix,
				runtimeClasspath,
				_sourceAttachmentResolver,
				refresh,
				entry -> generatedPrefix + entry.getFileName(),
				entry -> IntelliJRunConfigurationSupport.libraryMetadataFileName(generatedPrefix + entry.getFileName())
		);
		TemplateXmlWriter.write(
				launchPaths.ideaLaunchModulePath(),
				IntelliJRunConfigurationSupport.createLaunchModuleDocument(
						launchPaths.instanceRoot(),
						launchDependencyModuleNames(repository, moduleInjection, launchPaths.environment()),
						runtimeClasspath,
						entry -> generatedPrefix + entry.getFileName()
				)
		);
		IntelliJRunConfigurationSupport.registerModule(
				"$PROJECT_DIR$/.idea/modules/launch/fabric/"
				+ IntelliJModuleNames.fabricLaunchModuleFileName(
						repository.projectName(),
						launchPaths.environment().id(),
						launchPaths.platformId()
				)
		);
	}

	/**
	 * Resolves the IntelliJ module dependencies that should be built before the generated launch module runs.
	 *
	 * <p>These are intentionally `PROVIDED` module edges in the launch module metadata so IntelliJ's
	 * `Make` step rebuilds outputs before launch, without letting those modules leak their
	 * compile-only classpaths into the actual direct-DLI runtime.
	 *
	 * @param repository      the discovered repository context
	 * @param moduleInjection the resolved injected module contract
	 *
	 * @return the ordered module dependency names
	 */
	private List<String> launchDependencyModuleNames(
			RepositoryContext repository,
			FabricModuleInjection moduleInjection,
			LaunchEnvironment environment
	)
	{
		if (moduleInjection.moduleId() == null || moduleInjection.moduleId().isBlank())
		{
			return List.of();
		}

		var graph = repository.buildGraph();
		List<String> moduleNames = new ArrayList<>();

		for (var module : ModuleAggregationResolver.aggregatedModules(graph, moduleInjection.moduleId()))
		{
			moduleNames.add(IntelliJModuleNames.sourceSetModuleName(repository.projectName(), module.id(), SourceSetNames.MAIN));

			if (environment.isClient() && (!module.clientSources().isEmpty() || !module.clientResources().isEmpty()))
			{
				moduleNames.add(IntelliJModuleNames.sourceSetModuleName(repository.projectName(), module.id(), SourceSetNames.CLIENT));
			}
		}

		return moduleNames;
	}

	/**
	 * Writes the IntelliJ Application run configuration for the Fabric launch bundle.
	 *
	 * @param repository          the discovered repository context
	 * @param outputPath          the IntelliJ run configuration path
	 * @param fabricLaunch        the prepared Fabric launch configuration
	 * @param launchPaths         the generated launch path layout
	 * @param platformDisplayName the current platform display name
	 *
	 * @throws IOException if the run configuration cannot be written
	 */
	private void writeIdeaRunConfiguration(
			RepositoryContext repository,
			Path outputPath,
			VanillaLaunchConfig fabricLaunch,
			FabricModuleInjection moduleInjection,
			FabricLaunchPaths launchPaths,
			String platformDisplayName,
			boolean refresh
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("CONFIG_NAME", "Fabric " + launchPaths.environment().displayName() + " (" + platformDisplayName + ")");
		values.put("MAIN_CLASS_NAME", DEV_LAUNCH_MAIN_CLASS);
		values.put("MODULE_NAME", IntelliJModuleNames.fabricLaunchModuleName(repository.projectName(), launchPaths.environment().id(), launchPaths.platformId()));
		values.put("PROGRAM_PARAMETERS", IntelliJRunConfigurationSupport.renderIdeaArguments(fabricLaunch.gameArgs()));
		values.put("VM_PARAMETERS", IntelliJRunConfigurationSupport.renderIdeaArguments(IntelliJRunConfigurationSupport.ideaVmArguments(fabricLaunch.jvmArgs())));
		values.put("WORKING_DIRECTORY", IntelliJRunConfigurationSupport.xmlPath(fabricLaunch.workingDirectory()));
		values.put(
				"CLASSPATH_MODIFICATIONS",
				renderIdeaClasspathModifications(
						launchClasspathExclusions(repository, moduleInjection, fabricLaunch, launchPaths.environment(), refresh)
				)
		);
		var rendered = FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/intellij-run-config.xml",
				values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Resolves the compile-time classpath entries IntelliJ should exclude from a generated launch.
	 *
	 * <p>The launch module intentionally keeps `PROVIDED` module edges so IntelliJ will rebuild the
	 * requested modules before launch. IntelliJ also threads those compile libraries into the
	 * Application runtime classpath, so this exclusion list trims the run config back down to the
	 * exact prepared Fabric runtime classpath.
	 *
	 * @param repository      the discovered repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param fabricLaunch    the prepared Fabric launch configuration
	 * @param refresh         whether dependency resolution should refresh cached artifacts
	 *
	 * @return the ordered classpath exclusions
	 *
	 * @throws IOException if dependency resolution fails
	 */
	private List<Path> launchClasspathExclusions(
			RepositoryContext repository,
			FabricModuleInjection moduleInjection,
			VanillaLaunchConfig fabricLaunch,
			LaunchEnvironment environment,
			boolean refresh
	) throws IOException
	{
		if (moduleInjection.moduleId() == null || moduleInjection.moduleId().isBlank())
		{
			return List.of();
		}

		var graph = repository.buildGraph();
		Set<Path> runtimeClasspath = new LinkedHashSet<>();

		for (var entry : IntelliJRunConfigurationSupport.effectiveRuntimeClasspath(fabricLaunch))
		{
			runtimeClasspath.add(entry.toAbsolutePath().normalize());
		}

		Set<Path> exclusions = new LinkedHashSet<>();

		for (var module : ModuleAggregationResolver.aggregatedModules(graph, moduleInjection.moduleId()))
		{
			for (var dependency : _ideaDependencyResolver.resolveModuleLibraries(
					graph,
					repository.loaderVersion(),
					refresh,
					module,
					environment.isClient()
			))
			{
				var normalized = dependency.toAbsolutePath().normalize();

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
	 *
	 * @return the serialized XML fragment
	 */
	private String renderIdeaClasspathModifications(List<Path> exclusions)
	{
		if (exclusions.isEmpty())
		{
			return "<classpathModifications/>";
		}

		var builder = new StringBuilder();
		builder.append("<classpathModifications>");

		for (var exclusion : exclusions)
		{
			builder.append("<entry exclude=\"true\" path=\"")
			       .append(XmlEscaper.escapeAttribute(exclusion.toString()))
			       .append("\"/>");
		}

		builder.append("</classpathModifications>");
		return builder.toString();
	}
}
