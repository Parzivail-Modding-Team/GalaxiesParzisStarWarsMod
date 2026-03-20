package dev.pswg.toolchain.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.JsonNode;

import dev.pswg.toolchain.mojang.MojangMetadataClient;
import dev.pswg.toolchain.mojang.MojangPaths;
import dev.pswg.toolchain.mojang.model.MojangRule;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadataLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Prepares serialized vanilla launch configurations and IntelliJ run configurations.
 */
public final class VanillaLaunchService
{
	/**
	 * The IntelliJ module name for the standalone toolchain main source set.
	 */
	private static final String INTELLIJ_MODULE_NAME = "pswg-toolchain.main";

	/**
	 * The standalone Mojang metadata client.
	 */
	private final MojangMetadataClient _mojangClient;

	/**
	 * The shared JSON serializer.
	 */
	private final ObjectMapper _mapper;

	/**
	 * Creates a launch service with default runtime dependencies.
	 */
	public VanillaLaunchService()
	{
		_mojangClient = new MojangMetadataClient();
		_mapper = new ObjectMapper();
	}

	/**
	 * Prepares a vanilla client launch configuration and an IntelliJ run configuration file.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh whether to force fresh runtime downloads
	 * @return the prepared launch configuration
	 * @throws IOException if preparation fails
	 */
	public VanillaLaunchConfig prepareIntelliJLaunch(String versionId, boolean refresh) throws IOException
	{
		MojangVersionMetadata metadata = _mojangClient.getVersionMetadata(versionId, refresh);
		_mojangClient.downloadClientJar(versionId, refresh);
		_mojangClient.downloadRuntime(versionId, refresh);

		MojangPaths paths = _mojangClient.paths();
		Path projectRoot = Path.of("").toAbsolutePath().normalize();
		String platformId = currentPlatformId();
		String platformDisplayName = currentPlatformDisplayName();
		Path instanceRoot = paths.workRoot()
		                        .resolve("instances")
		                        .resolve("vanilla-client")
		                        .resolve(platformId)
		                        .resolve(versionId);
		Path gameDirectory = instanceRoot.resolve("game");
		Path nativesDirectory = instanceRoot.resolve("natives");
		Path launchConfigFile = instanceRoot.resolve("launch.json");
		Path ideaRunConfigurationFile = projectRoot.resolve(".idea")
		                                          .resolve("runConfigurations")
		                                          .resolve("Vanilla_Client_" + platformId.toUpperCase(Locale.ROOT) + ".xml");

		Files.createDirectories(gameDirectory);
		Files.createDirectories(nativesDirectory);
		Files.createDirectories(ideaRunConfigurationFile.getParent());

		List<Path> classpath = buildClasspath(versionId, metadata);
		extractNativeLibraries(metadata, nativesDirectory);
		Path loggingConfiguration = prepareLoggingConfiguration(instanceRoot, gameDirectory, metadata, refresh);

		Map<String, String> variables = buildLaunchVariables(versionId, metadata, gameDirectory, nativesDirectory, classpath, loggingConfiguration);
		List<String> jvmArgs = new ArrayList<>();
		jvmArgs.addAll(evaluateArguments(metadata.arguments() == null ? null : metadata.arguments().path("default-user-jvm"), variables));
		jvmArgs.addAll(evaluateArguments(metadata.arguments() == null ? null : metadata.arguments().path("jvm"), variables));

		if (metadata.logging() != null && metadata.logging().client() != null && metadata.logging().client().argument() != null && loggingConfiguration != null)
		{
			jvmArgs.add(metadata.logging().client().argument().replace("${path}", loggingConfiguration.toAbsolutePath().toString()));
		}

		List<String> gameArgs = evaluateArguments(metadata.arguments() == null ? null : metadata.arguments().path("game"), variables);

		VanillaLaunchConfig config = new VanillaLaunchConfig(
			versionId,
			metadata.mainClass(),
			findJavaExecutable(),
			gameDirectory,
			gameDirectory,
			paths.mojangRoot().resolve("assets"),
			metadata.assetIndex().id(),
			nativesDirectory,
			loggingConfiguration,
			classpath,
			jvmArgs,
			gameArgs
		);

		writeLaunchConfig(launchConfigFile, config);
		writeIntelliJRunConfiguration(projectRoot, ideaRunConfigurationFile, launchConfigFile, platformDisplayName);
		return config;
	}

	/**
	 * Builds the full runtime classpath for a selected Mojang version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param metadata the resolved version metadata
	 * @return the ordered runtime classpath
	 */
	private List<Path> buildClasspath(String versionId, MojangVersionMetadata metadata)
	{
		List<Path> classpath = new ArrayList<>();

		for (MojangVersionMetadataLibrary library : metadata.libraries())
		{
			if (!_mojangClient.isLibraryAllowed(library))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null || library.downloads().artifact().path() == null)
			{
				continue;
			}

			classpath.add(_mojangClient.paths().libraryFile(library.downloads().artifact().path()));
		}

		classpath.add(_mojangClient.paths().clientJarFile(versionId));
		return classpath;
	}

	/**
	 * Extracts native libraries from downloaded native jars into the chosen natives directory.
	 *
	 * @param metadata the resolved version metadata
	 * @param nativesDirectory the target natives directory
	 * @throws IOException if extraction fails
	 */
	private void extractNativeLibraries(MojangVersionMetadata metadata, Path nativesDirectory) throws IOException
	{
		Files.createDirectories(nativesDirectory);
		Set<Path> nativeJars = new LinkedHashSet<>();

		for (MojangVersionMetadataLibrary library : metadata.libraries())
		{
			if (!_mojangClient.isLibraryAllowed(library))
			{
				continue;
			}

			if (library.name() == null || !library.name().contains(":natives-"))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null || library.downloads().artifact().path() == null)
			{
				continue;
			}

			nativeJars.add(_mojangClient.paths().libraryFile(library.downloads().artifact().path()));
		}

		for (Path nativeJar : nativeJars)
		{
			try (InputStream inputStream = Files.newInputStream(nativeJar);
			     ZipInputStream zipInputStream = new ZipInputStream(inputStream))
			{
				ZipEntry entry;

				while ((entry = zipInputStream.getNextEntry()) != null)
				{
					if (entry.isDirectory())
					{
						continue;
					}

					String fileName = Path.of(entry.getName()).getFileName().toString();

					if (!isNativeLibrary(fileName))
					{
						continue;
					}

					Path target = nativesDirectory.resolve(fileName);

					try (OutputStream outputStream = Files.newOutputStream(
						target,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.WRITE
					))
					{
						zipInputStream.transferTo(outputStream);
					}
				}
			}
		}
	}

	/**
	 * Downloads the optional logging configuration file for a version.
	 *
	 * @param metadata the resolved version metadata
	 * @param refresh whether to force a fresh download
	 * @return the cached logging configuration path, or {@code null}
	 * @throws IOException if the logging configuration cannot be downloaded
	 */
	private Path prepareLoggingConfiguration(
		Path instanceRoot,
		Path gameDirectory,
		MojangVersionMetadata metadata,
		boolean refresh
	) throws IOException
	{
		Path generatedConfiguration = instanceRoot.resolve("config").resolve("log4j2-intellij.xml");
		Files.createDirectories(generatedConfiguration.getParent());
		String latestLog = xmlPath(gameDirectory.resolve("logs").resolve("latest.log"));
		String archivedLogs = xmlPath(gameDirectory.resolve("logs").resolve("%d{yyyy-MM-dd}-%i.log.gz"));
		String xml = """
			<?xml version="1.0" encoding="UTF-8"?>
			<Configuration status="WARN">
			    <Appenders>
			        <Console name="SysOut" target="SYSTEM_OUT">
			            <PatternLayout disableAnsi="false" noConsoleNoAnsi="false" pattern="%%style{[%%d{HH:mm:ss}]}{black} %%highlight{[%%t/%%level]} %%msg{nolookups}%%n%%throwable" />
			        </Console>
			        <RollingRandomAccessFile name="File" fileName="%s" filePattern="%s">
			            <PatternLayout pattern="[%%d{HH:mm:ss}] [%%t/%%level]: %%msg{nolookups}%%n%%throwable" />
			            <Policies>
			                <TimeBasedTriggeringPolicy />
			                <OnStartupTriggeringPolicy />
			            </Policies>
			        </RollingRandomAccessFile>
			    </Appenders>
			    <Loggers>
			        <Root level="info">
			            <filters>
			                <MarkerFilter marker="NETWORK_PACKETS" onMatch="DENY" onMismatch="NEUTRAL" />
			            </filters>
			            <AppenderRef ref="SysOut"/>
			            <AppenderRef ref="File"/>
			        </Root>
			    </Loggers>
			</Configuration>
			""".formatted(latestLog, archivedLogs);
		Files.writeString(generatedConfiguration, xml);

		if (metadata.logging() != null && metadata.logging().client() != null && metadata.logging().client().file() != null)
		{
			Path target = _mojangClient.paths().mojangRoot().resolve("logging").resolve(metadata.logging().client().file().id());
			_mojangClient.download(URI.create(metadata.logging().client().file().url()), target, refresh);
		}

		return generatedConfiguration;
	}

	/**
	 * Builds the substitution variables used by Mojang argument templates.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param metadata the resolved version metadata
	 * @param gameDirectory the game directory
	 * @param nativesDirectory the natives directory
	 * @param classpath the resolved classpath
	 * @param loggingConfiguration the optional logging configuration path
	 * @return the resolved variable map
	 */
	private Map<String, String> buildLaunchVariables(
		String versionId,
		MojangVersionMetadata metadata,
		Path gameDirectory,
		Path nativesDirectory,
		List<Path> classpath,
		Path loggingConfiguration
	)
	{
		Map<String, String> variables = new HashMap<>();
		String classpathSeparator = System.getProperty("path.separator");

		variables.put("auth_player_name", "Dev");
		variables.put("version_name", versionId);
		variables.put("game_directory", gameDirectory.toAbsolutePath().toString());
		variables.put("assets_root", _mojangClient.paths().mojangRoot().resolve("assets").toAbsolutePath().toString());
		variables.put("assets_index_name", metadata.assetIndex().id());
		variables.put("auth_uuid", "00000000-0000-0000-0000-000000000000");
		variables.put("auth_access_token", "0");
		variables.put("clientid", "0");
		variables.put("auth_xuid", "0");
		variables.put("version_type", metadata.id());
		variables.put("launcher_name", "PSWG Toolchain");
		variables.put("launcher_version", "0.1");
		variables.put("natives_directory", nativesDirectory.toAbsolutePath().toString());
		variables.put("classpath", classpath.stream().map(path -> path.toAbsolutePath().toString()).reduce((a, b) -> a + classpathSeparator + b).orElse(""));

		if (loggingConfiguration != null)
		{
			variables.put("path", loggingConfiguration.toAbsolutePath().toString());
		}

		return variables;
	}

	/**
	 * Evaluates a Mojang arguments array against the default launch context.
	 *
	 * @param argumentsNode the raw arguments node
	 * @param variables the substitution variables
	 * @return the resolved argument list
	 */
	private List<String> evaluateArguments(JsonNode argumentsNode, Map<String, String> variables)
	{
		List<String> resolved = new ArrayList<>();

		if (argumentsNode == null || argumentsNode.isMissingNode() || argumentsNode.getNodeType() != JsonNodeType.ARRAY)
		{
			return resolved;
		}

		ArrayNode arrayNode = (ArrayNode) argumentsNode;

		for (JsonNode entry : arrayNode)
		{
			if (entry.isTextual())
			{
				resolved.add(substitute(entry.asText(), variables));
				continue;
			}

			if (!entry.isObject())
			{
				continue;
			}

			if (!isAllowed(entry.path("rules")))
			{
				continue;
			}

			JsonNode valueNode = entry.path("value");

			if (valueNode.isTextual())
			{
				resolved.add(substitute(valueNode.asText(), variables));
				continue;
			}

			if (valueNode.isArray())
			{
				for (JsonNode valueEntry : valueNode)
				{
					if (valueEntry.isTextual())
					{
						resolved.add(substitute(valueEntry.asText(), variables));
					}
				}
			}
		}

		return resolved;
	}

	/**
	 * Checks whether a Mojang raw argument rule set is allowed for the current runtime.
	 *
	 * @param rulesNode the raw rules node
	 * @return {@code true} if the entry should be included
	 */
	private boolean isAllowed(JsonNode rulesNode)
	{
		if (rulesNode == null || rulesNode.isMissingNode() || !rulesNode.isArray() || rulesNode.isEmpty())
		{
			return true;
		}

		boolean allowed = false;

		for (JsonNode ruleNode : rulesNode)
		{
			if (!ruleNode.isObject())
			{
				continue;
			}

			if (ruleNode.hasNonNull("features"))
			{
				continue;
			}

			MojangRule rule = _mapper.convertValue(ruleNode, MojangRule.class);

			if (_mojangClient.matchesRule(rule))
			{
				if ("allow".equals(rule.action()))
				{
					allowed = true;
				}
				else if ("disallow".equals(rule.action()))
				{
					allowed = false;
				}
			}
		}

		return allowed;
	}

	/**
	 * Applies variable substitution to a Mojang argument template string.
	 *
	 * @param value the raw template string
	 * @param variables the variable map
	 * @return the substituted value
	 */
	private String substitute(String value, Map<String, String> variables)
	{
		String substituted = value;

		for (Map.Entry<String, String> entry : variables.entrySet())
		{
			substituted = substituted.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		return substituted;
	}

	/**
	 * Writes a serialized launch configuration file.
	 *
	 * @param path the launch configuration output path
	 * @param config the launch configuration
	 * @throws IOException if the file cannot be written
	 */
	private void writeLaunchConfig(Path path, VanillaLaunchConfig config) throws IOException
	{
		Files.createDirectories(path.getParent());
		_mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), config);
	}

	/**
	 * Writes an IntelliJ Application run configuration that launches the bootstrap main.
	 *
	 * @param path the IntelliJ run configuration output path
	 * @param launchConfigPath the serialized launch configuration path
	 * @throws IOException if the file cannot be written
	 */
	private void writeIntelliJRunConfiguration(
		Path projectRoot,
		Path path,
		Path launchConfigPath,
		String platformDisplayName
	) throws IOException
	{
		String moduleName = readIntelliJModuleName(projectRoot);
		String launchConfigValue = "&quot;$PROJECT_DIR$/"
			+ projectRoot.relativize(launchConfigPath.toAbsolutePath().normalize()).toString().replace('\\', '/')
			+ "&quot;";
		String xml = """
			<component name="ProjectRunConfigurationManager">
			  <configuration default="false" factoryName="Application" name="%s" type="Application">
			    <option name="MAIN_CLASS_NAME" value="dev.pswg.toolchain.runtime.VanillaLaunchMain"/>
			    <module name="%s"/>
			    <option name="PROGRAM_PARAMETERS" value="%s"/>
			    <shortenClasspath name="ARGS_FILE"/>
			    <option name="WORKING_DIRECTORY" value="$PROJECT_DIR$"/>
			    <method v="2">
			      <option enabled="true" name="Make"/>
			    </method>
			  <classpathModifications/></configuration>
			</component>
			""".formatted(
			"Vanilla Client (" + platformDisplayName + ")",
			moduleName,
			launchConfigValue
		);

		Files.createDirectories(path.getParent());
		Files.writeString(path, xml);
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

		return INTELLIJ_MODULE_NAME;
	}

	/**
	 * Resolves the Java executable path for child launch processes.
	 *
	 * @return the Java executable path
	 */
	private String findJavaExecutable()
	{
		return Path.of(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java").toString();
	}

	/**
	 * Checks whether the current runtime is Windows.
	 *
	 * @return {@code true} if running on Windows
	 */
	private boolean isWindows()
	{
		return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("windows");
	}

	/**
	 * Gets the current runtime platform identifier for launch file partitioning.
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
	 * Gets the current runtime platform display name for IntelliJ run configurations.
	 *
	 * @return the current platform display name
	 */
	private String currentPlatformDisplayName()
	{
		return switch (currentPlatformId())
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

	/**
	 * Checks whether a file is a native library candidate.
	 *
	 * @param fileName the file name to inspect
	 * @return {@code true} if the file should be extracted as a native library
	 */
	private boolean isNativeLibrary(String fileName)
	{
		String lower = fileName.toLowerCase(Locale.ROOT);
		return lower.endsWith(".dll") || lower.endsWith(".so") || lower.endsWith(".dylib") || lower.endsWith(".jnilib");
	}

}
