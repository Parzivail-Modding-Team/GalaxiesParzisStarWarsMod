package dev.pswg.toolchain.fabric;

import dev.pswg.toolchain.definition.PswgBuildDefinition;
import dev.pswg.toolchain.mojang.MojangPaths;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.ModuleSpec;
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
import java.util.Optional;
import java.util.Properties;

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
		return prepareClientLaunch(versionId, refresh, null);
	}

	/**
	 * Prepares a Fabric-style development launch bundle for the client environment.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force fresh runtime downloads
	 * @param moduleId the optional PSWG module identifier to inject
	 * @return the prepared launch configuration
	 * @throws IOException if generation fails
	 */
	public VanillaLaunchConfig prepareClientLaunch(String versionId, boolean refresh, String moduleId) throws IOException
	{
		VanillaLaunchConfig vanillaLaunch = new VanillaLaunchService().prepareIntelliJLaunch(versionId, refresh);
		String loaderVersion = loadGradleProperties().getProperty("loader_version");
		FabricRuntimeArtifacts runtimeArtifacts = _runtimeResolver.resolveClientRuntime(loaderVersion, refresh);
		Path projectRoot = Path.of("").toAbsolutePath().normalize();
		List<Path> moduleRoots = resolveModuleRuntimeRoots(projectRoot.getParent(), moduleId);
		String platformId = currentPlatformId();
		String platformDisplayName = currentPlatformDisplayName(platformId);
		Path instanceRoot = projectRoot.resolve("work")
		                               .resolve("instances")
		                               .resolve("fabric-client")
		                               .resolve(platformId)
		                               .resolve(versionId);
		Path configDirectory = instanceRoot.resolve("config");
		Path launchConfigPath = configDirectory.resolve("launch.cfg");
		Path loggingConfigPath = configDirectory.resolve("log4j2-intellij.xml");
		Path serializedLaunchPath = instanceRoot.resolve("launch.json");
		Path ideaRunConfigurationPath = projectRoot.resolve(".idea")
		                                          .resolve("runConfigurations")
		                                          .resolve("Fabric_Client_" + platformId.toUpperCase(Locale.ROOT) + ".xml");

		Files.createDirectories(configDirectory);
		Files.createDirectories(instanceRoot);
		writeLoggingConfig(vanillaLaunch, loggingConfigPath);
		prepareFabricAssetIndex(versionId, vanillaLaunch.assetIndexId());
		writeDevLaunchConfig(versionId, vanillaLaunch, moduleRoots, launchConfigPath, loggingConfigPath);

		List<String> jvmArgs = new ArrayList<>(vanillaLaunch.jvmArgs());
		List<Path> prependedClasspath = new ArrayList<>(moduleRoots);
		prependedClasspath.addAll(runtimeArtifacts.classpath());
		replaceClasspath(jvmArgs, prependedClasspath);
		jvmArgs.removeIf(argument -> argument.startsWith("-Dlog4j.configurationFile="));
		jvmArgs.add("-Dfabric.dli.config=" + launchConfigPath.toAbsolutePath());
		jvmArgs.add("-Dfabric.dli.env=client");
		jvmArgs.add("-Dfabric.dli.main=" + runtimeArtifacts.runtimeMainClass());
		jvmArgs.add("-Dfabric.development=true");
		jvmArgs.add("-Dlog4j.configurationFile=" + loggingConfigPath.toAbsolutePath());
		jvmArgs.add("-Dlog4j2.formatMsgNoLookups=true");
		jvmArgs.add("-Dfabric.log.disableAnsi=false");
		addIfMissing(jvmArgs, "--sun-misc-unsafe-memory-access=allow");
		addIfMissing(jvmArgs, "--enable-native-access=ALL-UNNAMED");

		if (runtimeArtifacts.mixinJavaAgentJar() != null)
		{
			addIfMissing(jvmArgs, "-javaagent:" + runtimeArtifacts.mixinJavaAgentJar().toAbsolutePath());
		}

		VanillaLaunchConfig fabricLaunch = new VanillaLaunchConfig(
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
			List.of("--username", "Dev")
		);

		writeLaunchJson(serializedLaunchPath, fabricLaunch);
		writeIdeaRunConfiguration(projectRoot, ideaRunConfigurationPath, serializedLaunchPath, platformDisplayName);
		return fabricLaunch;
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
		values.put("FABRIC_DEFAULT_MOD_DISTRIBUTION_NAMESPACE", "official");
		values.put("FABRIC_DEFAULT_MIXIN_REMAP_TYPE", "static");
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

		return "\tfabric.classPathGroups=" + joinedRoots + "\n";
	}

	/**
	 * Resolves the runtime-visible output roots for an injected PSWG module.
	 *
	 * @param projectRoot the tracked repository root
	 * @param moduleId the optional module identifier
	 * @return the ordered classpath roots to inject
	 */
	private List<Path> resolveModuleRuntimeRoots(Path projectRoot, String moduleId)
	{
		if (moduleId == null || moduleId.isBlank())
		{
			return List.of();
		}

		BuildGraph graph = new PswgBuildDefinition().define();
		Optional<ModuleSpec> module = graph.modules()
		                                  .stream()
		                                  .filter(candidate -> moduleId.equals(candidate.id()))
		                                  .findFirst();

		if (module.isEmpty())
		{
			throw new IllegalArgumentException("Unknown module id: " + moduleId);
		}

		return resolveOutputRoots(projectRoot, module.get());
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
		roots.addAll(resolveSourceSetOutputRoots(projectRoot, module, "main"));
		roots.addAll(resolveSourceSetOutputRoots(projectRoot, module, "client"));

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
		String moduleName = readProjectName(projectRoot) + ".projects." + module.id() + "." + sourceSetName;
		Path intellijOutput = projectRoot.resolve("out").resolve("production").resolve(moduleName);

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
