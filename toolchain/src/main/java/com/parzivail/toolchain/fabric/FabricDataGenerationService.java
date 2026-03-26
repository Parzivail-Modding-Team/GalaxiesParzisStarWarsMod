package com.parzivail.toolchain.fabric;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parzivail.toolchain.intellij.IntelliJModuleNames;
import com.parzivail.toolchain.intellij.IntelliJRunConfigurationSupport;
import com.parzivail.toolchain.model.*;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.runtime.LaunchIdentity;
import com.parzivail.toolchain.runtime.VanillaLaunchConfig;
import com.parzivail.toolchain.runtime.VanillaLaunchService;
import com.parzivail.toolchain.source.SourceAttachmentResolver;
import com.parzivail.toolchain.template.FileTemplateRenderer;
import com.parzivail.toolchain.template.TemplateXmlWriter;
import com.parzivail.toolchain.util.HostPlatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Generates module-scoped Fabric API datagen run configurations.
 *
 * <p>This service prepares one client-derived Fabric launch bundle that
 * contains the full modeled runtime surface, then emits one IntelliJ run
 * configuration per datagen-capable module. Each generated run configuration
 * pins both the Fabric mod id and the checked-in output directory for that
 * module so developers cannot accidentally run one module's datagen against
 * another's output roots.
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
	 * The shared JSON serializer.
	 */
	private final ObjectMapper _mapper;

	/**
	 * The Fabric runtime resolver.
	 */
	private final FabricRuntimeResolver _runtimeResolver;

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
		_sourceAttachmentResolver = new SourceAttachmentResolver();
	}

	/**
	 * The generated datagen configuration metadata for one module.
	 *
	 * @param moduleId             the logical module identifier
	 * @param fabricModId          the Fabric mod identifier selected for datagen
	 * @param outputDirectory      the checked-in datagen output directory
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
	 * @param versionId         the Minecraft version identifier
	 * @param refresh           whether cached runtime artifacts should be refreshed
	 * @param requestedModuleId the optional requested datagen module identifier
	 * @param identity          the client launch identity to embed
	 *
	 * @return the generated datagen configurations
	 *
	 * @throws IOException if generation fails
	 */
	public List<DatagenConfiguration> prepareRunConfigurations(
			String versionId,
			boolean refresh,
			String requestedModuleId,
			LaunchIdentity identity
	) throws IOException
	{
		var repository = RepositoryContext.load();
		var targets = datagenTargets(repository.buildGraph(), requestedModuleId);
		var aggregateModuleId = repository.buildGraph().developmentModuleId();
		var effectiveIdentity = LaunchEnvironment.CLIENT.effectiveIdentity(identity);
		var vanillaLaunch = new VanillaLaunchService().prepareRuntime(
				versionId,
				refresh,
				LaunchEnvironment.CLIENT,
				effectiveIdentity
		);
		var runtimeArtifacts = resolveFabricRuntimeArtifacts(repository.loaderVersion(), refresh);
		var moduleInjection = resolveModuleInjection(repository, aggregateModuleId, refresh);
		var launchPaths = createLaunchPaths(
				repository.projectName(),
				versionId
		);

		prepareLaunchFiles(versionId, vanillaLaunch, moduleInjection.moduleRoots(), launchPaths);
		var jvmArgs = buildFabricJvmArgs(vanillaLaunch, runtimeArtifacts, moduleInjection, launchPaths);
		var datagenLaunch = createDatagenLaunchConfig(
				versionId,
				jvmArgs,
				launchPaths.loggingConfigPath(),
				vanillaLaunch,
				launchPaths,
				effectiveIdentity
		);

		FabricLaunchSupport.writeLaunchJson(_mapper, launchPaths.serializedLaunchPath(), datagenLaunch);
		writeIdeaLaunchModule(repository, moduleInjection, datagenLaunch, launchPaths, refresh);
		return writeIdeaRunConfigurations(repository, datagenLaunch, launchPaths, targets);
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
			boolean refresh
	) throws IOException
	{
		return _runtimeResolver.resolveRuntime(loaderVersion, refresh, LaunchEnvironment.CLIENT);
	}

	/**
	 * Resolves the aggregate module injection contract used by datagen launches.
	 *
	 * <p>Datagen needs the full modeled module surface so downstream generators can read the
	 * checked-in resources and generated output of upstream modules. The per-module output boundary
	 * is enforced by the generated run configuration's `fabric-api.datagen.modid` and output-dir
	 * properties, not by shrinking the runtime classpath to one module.
	 *
	 * @param repository the discovered repository context
	 * @param moduleId   the aggregate module identifier used for datagen launches
	 * @param refresh    whether to revalidate cached external runtime artifacts
	 *
	 * @return the resolved module injection contract
	 *
	 * @throws IOException if supporting external runtime dependencies cannot be resolved
	 */
	private FabricModuleInjection resolveModuleInjection(
			RepositoryContext repository,
			String moduleId,
			boolean refresh
	) throws IOException
	{
		var graph = repository.buildGraph();
		var projectName = repository.projectName();
		var rootModule = ModuleAggregationResolver.requireModule(graph, moduleId);
		var moduleRoots = resolveOutputRoots(projectName, rootModule);
		List<Path> dependencyRoots = new ArrayList<>();
		List<MavenDependencySpec> runtimeDependencies = new ArrayList<>(rootModule.runtimeDependencies());

		for (var dependencyModule : ModuleAggregationResolver.aggregatedDependencies(graph, moduleId))
		{
			dependencyRoots.addAll(resolveOutputRoots(projectName, dependencyModule));
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
	 * Creates the standard path layout for the aggregate Fabric datagen launch bundle.
	 *
	 * @param projectName the IntelliJ project name
	 * @param versionId   the Minecraft version identifier
	 *
	 * @return the derived launch paths
	 */
	private FabricDatagenPaths createLaunchPaths(
			String projectName,
			String versionId
	)
	{
		var platform = HostPlatform.current();
		var platformId = platform.id();
		var instanceRoot = ToolchainPaths.getDatagenInstanceRoot(versionId, platform);
		var configDirectory = instanceRoot.resolve(ToolchainPaths.INSTANCE_CONFIG_ROOT_NAME);

		return new FabricDatagenPaths(
				platformId,
				platform.displayName(),
				instanceRoot,
				configDirectory.resolve("launch.cfg"),
				configDirectory.resolve("log4j2-intellij.xml"),
				instanceRoot.resolve("launch.json"),
				ToolchainPaths.INTELLIJ_FABRIC_LAUNCH_MODULE_DIRECTORY
						.resolve(IntelliJModuleNames.fabricDatagenLaunchModuleFileName(projectName, platformId))
		);
	}

	/**
	 * Prepares generated files that sit beside the aggregate Fabric datagen launch bundle.
	 *
	 * @param versionId     the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 * @param moduleRoots   the injected grouped module roots that should be exposed as one logical mod
	 * @param launchPaths   the generated launch path layout
	 *
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
		Files.createDirectories(launchPaths.instanceRoot().resolve(ToolchainPaths.INSTANCE_GAME_ROOT_NAME));
		FabricLaunchSupport.writeLoggingConfig(vanillaLaunch, launchPaths.loggingConfigPath());
		FabricLaunchSupport.prepareFabricAssetIndex(versionId, vanillaLaunch.assetIndexId());
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
			FabricDatagenPaths launchPaths
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
		FabricLaunchSupport.replaceLoggingConfiguration(
				jvmArgs,
				launchPaths.loggingConfigPath(),
				FabricDevLaunchService.DEFAULT_ANSI_LOGGING_ENABLED
		);
		addFabricRuntimeProperties(jvmArgs, runtimeArtifacts, launchPaths);
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
			FabricDatagenPaths launchPaths
	)
	{
		jvmArgs.add("-Dfabric.dli.config=" + launchPaths.launchConfigPath().toAbsolutePath());
		jvmArgs.add("-Dfabric.dli.env=" + LaunchEnvironment.CLIENT.id());
		jvmArgs.add("-Dfabric.dli.main=" + runtimeArtifacts.runtimeMainClass());
		jvmArgs.add("-Dfabric.development=true");
	}

	/**
	 * Creates the final serialized aggregate Fabric datagen launch config.
	 *
	 * @param versionId         the Minecraft version identifier
	 * @param jvmArgs           the resolved JVM arguments
	 * @param loggingConfigPath the generated logging configuration path
	 * @param vanillaLaunch     the prepared vanilla launch baseline
	 * @param launchPaths       the generated launch path layout
	 * @param identity          the launch-time player identity
	 *
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
		var datagenWorkingDirectory = launchPaths.instanceRoot().resolve(ToolchainPaths.INSTANCE_GAME_ROOT_NAME);

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
			Path loggingConfigPath
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("LOG4J_CONFIGURATION_FILE", loggingConfigPath.toAbsolutePath().toString());
		values.put("FABRIC_DEFAULT_MOD_DISTRIBUTION_NAMESPACE", FabricDevLaunchService.DEFAULT_MOD_DISTRIBUTION_NAMESPACE);
		values.put("FABRIC_DEFAULT_MIXIN_REMAP_TYPE", FabricDevLaunchService.DEFAULT_MIXIN_REMAP_TYPE);
		values.put("OPTIONAL_COMMON_PROPERTIES", FabricLaunchSupport.optionalCommonProperties(moduleRoots));
		values.put("OPTIONAL_ENVIRONMENT_COMMON_PROPERTIES", FabricLaunchSupport.environmentCommonProperties(versionId));
		values.put("OPTIONAL_ENVIRONMENT_PROPERTIES_SECTION", FabricLaunchSupport.clientPropertiesSection(versionId));
		values.put("ENVIRONMENT_ARGS_SECTION", FabricLaunchSupport.clientArgsSection(versionId, vanillaLaunch));
		var rendered = FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/fabric-dev-launch.cfg",
				values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Resolves the module targets that should receive datagen run configurations.
	 *
	 * @param graph             the authoritative build graph
	 * @param requestedModuleId the optional requested module id
	 *
	 * @return the ordered datagen targets
	 */
	private List<ModuleSpec> datagenTargets(
			BuildGraph graph,
			String requestedModuleId
	)
	{
		var supportedModules = graph.modules()
		                            .stream()
		                            .filter(this::supportsDatagen)
		                            .toList();

		if (requestedModuleId == null || requestedModuleId.isBlank())
		{
			return supportedModules;
		}

		var requested = ModuleAggregationResolver.requireModule(graph, requestedModuleId);

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
	 *
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
	 * Derived path layout for the aggregate Fabric datagen launch bundle.
	 *
	 * @param platformId           the current platform identifier
	 * @param platformDisplayName  the current platform display name
	 * @param instanceRoot         the Fabric datagen instance root
	 * @param launchConfigPath     the generated DLI launch config path
	 * @param loggingConfigPath    the generated log4j configuration path
	 * @param serializedLaunchPath the serialized launch JSON path
	 * @param ideaLaunchModulePath the generated IntelliJ launch module path
	 */
	private record FabricDatagenPaths(
			String platformId,
			String platformDisplayName,
			Path instanceRoot,
			Path launchConfigPath,
			Path loggingConfigPath,
			Path serializedLaunchPath,
			Path ideaLaunchModulePath
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
	 * @param moduleId                 the injected aggregate module identifier
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
			ModuleSpec module
	)
	{
		return FabricModuleOutputResolver.resolveOutputRoots(
				projectName,
				module,
				true,
				"datagen",
				false
		);
	}

	/**
	 * Writes the generated IntelliJ launch module that owns the datagen runtime classpath.
	 *
	 * @param repository      the discovered repository context
	 * @param moduleInjection the resolved injected module contract
	 * @param datagenLaunch   the prepared datagen launch configuration
	 * @param launchPaths     the generated launch path layout
	 *
	 * @throws IOException if the launch module metadata cannot be written
	 */
	private void writeIdeaLaunchModule(
			RepositoryContext repository,
			FabricModuleInjection moduleInjection,
			VanillaLaunchConfig datagenLaunch,
			FabricDatagenPaths launchPaths,
			boolean refresh
	) throws IOException
	{
		var runtimeClasspath = IntelliJRunConfigurationSupport.effectiveRuntimeClasspath(datagenLaunch);
		var generatedPrefix = "fabric-datagen-" + launchPaths.platformId() + "-";
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
						launchDependencyModuleNames(repository, moduleInjection),
						runtimeClasspath,
						entry -> generatedPrefix + entry.getFileName()
				)
		);
		IntelliJRunConfigurationSupport.registerModule(
				"$PROJECT_DIR$/.idea/modules/launch/fabric/"
				+ IntelliJModuleNames.fabricDatagenLaunchModuleFileName(
						repository.projectName(),
						launchPaths.platformId()
				)
		);
	}

	/**
	 * Resolves the IntelliJ module dependencies that should be built before the datagen launch runs.
	 *
	 * @param repository      the discovered repository context
	 * @param moduleInjection the resolved injected module contract
	 *
	 * @return the ordered module dependency names
	 */
	private List<String> launchDependencyModuleNames(
			RepositoryContext repository,
			FabricModuleInjection moduleInjection
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

			if (!module.clientSources().isEmpty() || !module.clientResources().isEmpty())
			{
				moduleNames.add(IntelliJModuleNames.sourceSetModuleName(repository.projectName(), module.id(), SourceSetNames.CLIENT));
			}
		}

		return moduleNames;
	}

	/**
	 * Writes one IntelliJ Application run configuration per datagen-capable module.
	 *
	 * @param repository    the discovered repository context
	 * @param datagenLaunch the prepared aggregate datagen launch configuration
	 * @param launchPaths   the generated launch path layout
	 * @param targets       the datagen targets to emit
	 *
	 * @return the generated datagen configuration metadata
	 *
	 * @throws IOException if the run configurations cannot be written
	 */
	private List<DatagenConfiguration> writeIdeaRunConfigurations(
			RepositoryContext repository,
			VanillaLaunchConfig datagenLaunch,
			FabricDatagenPaths launchPaths,
			List<ModuleSpec> targets
	) throws IOException
	{
		List<DatagenConfiguration> generated = new ArrayList<>();
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (var target : targets)
		{
			var outputDirectory = ToolchainPaths.PROJECT_ROOT.resolve(target.datagenOutput());
			var runConfigurationPath = ToolchainPaths.INTELLIJ_RUN_CONFIGS_DIRECTORY.resolve(IntelliJModuleNames.fabricDatagenRunConfigurationFileName(target.id(), launchPaths.platformId()));
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

		deleteObsoleteRunConfigurations(launchPaths.platformId(), expectedFileNames);
		return List.copyOf(generated);
	}

	/**
	 * Deletes obsolete generated datagen run configurations after regeneration.
	 *
	 * @param platformId        the target platform identifier
	 * @param expectedFileNames the expected generated run-configuration file names
	 *
	 * @throws IOException if stale run-configuration files cannot be removed
	 */
	private void deleteObsoleteRunConfigurations(
			String platformId,
			Set<String> expectedFileNames
	) throws IOException
	{
		if (!Files.isDirectory(ToolchainPaths.INTELLIJ_RUN_CONFIGS_DIRECTORY))
		{
			return;
		}

		var suffix = "_" + platformId.toUpperCase() + ".xml";

		try (var entries = Files.list(ToolchainPaths.INTELLIJ_RUN_CONFIGS_DIRECTORY))
		{
			for (var entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				var fileName = entry.getFileName().toString();

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
	 * @param repository      the discovered repository context
	 * @param outputPath      the IntelliJ run configuration path
	 * @param datagenLaunch   the prepared aggregate datagen launch configuration
	 * @param launchPaths     the generated launch path layout
	 * @param target          the target datagen module
	 * @param outputDirectory the checked-in datagen output directory
	 *
	 * @throws IOException if the run configuration cannot be written
	 */
	private void writeIdeaRunConfiguration(
			RepositoryContext repository,
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
		values.put("PROGRAM_PARAMETERS", IntelliJRunConfigurationSupport.renderIdeaArguments(datagenLaunch.gameArgs()));
		values.put("VM_PARAMETERS", IntelliJRunConfigurationSupport.renderIdeaArguments(datagenVmArguments(datagenLaunch.jvmArgs(), target, outputDirectory)));
		values.put("WORKING_DIRECTORY", IntelliJRunConfigurationSupport.xmlPath(datagenLaunch.workingDirectory()));
		values.put("CLASSPATH_MODIFICATIONS", "<classpathModifications/>");
		var rendered = FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/intellij-run-config.xml",
				values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Filters the prepared JVM arguments down to the values IntelliJ should pass directly, then
	 * appends the module-scoped Fabric API datagen properties.
	 *
	 * @param jvmArgs         the prepared launch JVM arguments
	 * @param target          the target datagen module
	 * @param outputDirectory the checked-in datagen output directory
	 *
	 * @return the IntelliJ VM arguments
	 */
	private List<String> datagenVmArguments(
			List<String> jvmArgs,
			ModuleSpec target,
			Path outputDirectory
	)
	{
		var arguments = IntelliJRunConfigurationSupport.ideaVmArguments(jvmArgs);
		arguments.add(FABRIC_DATAGEN_FLAG);
		arguments.add(FABRIC_DATAGEN_OUTPUT_DIR_PROPERTY + outputDirectory.toAbsolutePath());
		arguments.add(FABRIC_DATAGEN_MOD_ID_PROPERTY + target.fabricModId());
		return arguments;
	}
}
