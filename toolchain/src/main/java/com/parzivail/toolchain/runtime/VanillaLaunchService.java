package com.parzivail.toolchain.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.parzivail.toolchain.mojang.MojangMetadataClient;
import com.parzivail.toolchain.mojang.model.MojangRule;
import com.parzivail.toolchain.mojang.model.MojangVersionMetadata;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.template.FileTemplateRenderer;
import com.parzivail.toolchain.template.XmlEscaper;
import com.parzivail.toolchain.util.HostPlatform;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Prepares the shared Mojang runtime baselines used by generated development launches.
 */
public final class VanillaLaunchService
{
	/**
	 * The launcher brand exposed to Minecraft.
	 */
	public static final String DEFAULT_LAUNCHER_NAME = "Toolchain";

	/**
	 * The launcher version exposed to Minecraft.
	 */
	public static final String DEFAULT_LAUNCHER_VERSION = "0.1";

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
	 * Prepares the shared Mojang runtime baseline for one environment.
	 *
	 * @param versionId   the Minecraft version identifier
	 * @param refresh     whether to force fresh runtime downloads
	 * @param environment the launch environment
	 * @param identity    the requested client identity
	 *
	 * @return the prepared launch configuration
	 *
	 * @throws IOException if preparation fails
	 */
	public VanillaLaunchConfig prepareRuntime(
			String versionId,
			boolean refresh,
			LaunchEnvironment environment,
			LaunchIdentity identity
	) throws IOException
	{
		var metadata = prepareVanillaRuntime(versionId, refresh, environment);
		var launchPaths = createLaunchPaths(versionId, environment);
		prepareLaunchDirectories(launchPaths);
		List<Path> bundledServerLibraries = environment.isServer()
		                                    ? _mojangClient.extractBundledServerLibraries(versionId, refresh)
		                                    : List.of();
		var classpath = buildClasspath(versionId, metadata, environment, bundledServerLibraries);

		if (environment.isClient())
		{
			extractNativeLibraries(metadata, launchPaths.nativesDirectory());
		}

		var loggingConfiguration = prepareLoggingConfiguration(
				launchPaths.instanceRoot(),
				launchPaths.gameDirectory(),
				metadata,
				refresh
		);
		var effectiveIdentity = environment.effectiveIdentity(identity);
		var variables = buildLaunchVariables(
				versionId,
				metadata,
				launchPaths.gameDirectory(),
				launchPaths.nativesDirectory(),
				classpath,
				loggingConfiguration,
				effectiveIdentity
		);
		var jvmArgs = environment.isClient()
		              ? buildJvmArgs(metadata, variables, loggingConfiguration)
		              : buildServerJvmArgs(metadata, variables);
		List<String> gameArgs = environment.isClient()
		                        ? buildGameArgs(metadata, variables)
		                        : List.of();

		return new VanillaLaunchConfig(
				versionId,
				// TODO: can the server main be part of the metadata instead of having a special case?
				environment.isClient() ? metadata.mainClass() : "net.minecraft.server.Main",
				findJavaExecutable(),
				launchPaths.gameDirectory(),
				launchPaths.gameDirectory(),
				ToolchainPaths.MOJANG_ASSETS_ROOT,
				metadata.assetIndex().id(),
				launchPaths.nativesDirectory(),
				loggingConfiguration,
				classpath,
				jvmArgs,
				gameArgs
		);
	}

	/**
	 * Resolves the vanilla runtime inputs required before launch config assembly.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param refresh   whether to force fresh downloads
	 *
	 * @return the resolved Mojang version metadata
	 *
	 * @throws IOException if the runtime cannot be prepared
	 */
	private MojangVersionMetadata prepareVanillaRuntime(
			String versionId,
			boolean refresh,
			LaunchEnvironment environment
	) throws IOException
	{
		var metadata = _mojangClient.getVersionMetadata(versionId, refresh);

		if (environment.isClient())
		{
			_mojangClient.downloadClientJar(versionId, refresh);
			_mojangClient.downloadRuntime(versionId, refresh);
			return metadata;
		}

		_mojangClient.downloadServerJar(versionId, refresh);
		downloadLibraries(metadata, refresh);
		return metadata;
	}

	/**
	 * Creates the standard path layout for the shared Mojang runtime baseline.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the derived launch paths
	 */
	private VanillaLaunchPaths createLaunchPaths(String versionId, LaunchEnvironment environment)
	{
		var instanceRoot = ToolchainPaths.getInstanceRoot(versionId, environment, HostPlatform.current());

		return new VanillaLaunchPaths(
				instanceRoot,
				instanceRoot.resolve("game"),
				instanceRoot.resolve("natives")
		);
	}

	/**
	 * Creates the directories needed by a generated vanilla launch bundle.
	 *
	 * @param launchPaths the generated path layout
	 *
	 * @throws IOException if any directory cannot be created
	 */
	private void prepareLaunchDirectories(VanillaLaunchPaths launchPaths) throws IOException
	{
		Files.createDirectories(launchPaths.gameDirectory());
		Files.createDirectories(launchPaths.nativesDirectory());
	}

	/**
	 * Downloads the runtime libraries declared by version metadata.
	 *
	 * @param metadata the resolved version metadata
	 * @param refresh  whether to revalidate cached artifacts
	 *
	 * @throws IOException if any declared library cannot be downloaded
	 */
	private void downloadLibraries(MojangVersionMetadata metadata, boolean refresh) throws IOException
	{
		for (var library : metadata.libraries())
		{
			if (!_mojangClient.isLibraryAllowed(library))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null)
			{
				continue;
			}

			var target = ToolchainPaths.mojangLibraryFile(library.downloads().artifact().path());
			_mojangClient.download(
					URI.create(library.downloads().artifact().url()),
					target,
					refresh
			);
		}
	}

	/**
	 * Builds the JVM argument list from Mojang version metadata.
	 *
	 * @param metadata             the resolved Mojang version metadata
	 * @param variables            the argument substitution variables
	 * @param loggingConfiguration the optional logging configuration path
	 *
	 * @return the resolved JVM arguments
	 */
	private List<String> buildJvmArgs(
			MojangVersionMetadata metadata,
			Map<String, String> variables,
			Path loggingConfiguration
	)
	{
		List<String> jvmArgs = new ArrayList<>();
		jvmArgs.addAll(evaluateArguments(metadata.arguments() == null ? null : metadata.arguments().path("default-user-jvm"), variables));
		jvmArgs.addAll(evaluateArguments(metadata.arguments() == null ? null : metadata.arguments().path("jvm"), variables));

		if (metadata.logging() != null && metadata.logging().client() != null && metadata.logging().client().argument() != null && loggingConfiguration != null)
		{
			jvmArgs.add(metadata.logging().client().argument().replace("${path}", loggingConfiguration.toAbsolutePath().toString()));
		}

		return jvmArgs;
	}

	/**
	 * Builds the game argument list from Mojang version metadata.
	 *
	 * @param metadata  the resolved Mojang version metadata
	 * @param variables the argument substitution variables
	 *
	 * @return the resolved game arguments
	 */
	private List<String> buildGameArgs(MojangVersionMetadata metadata, Map<String, String> variables)
	{
		return evaluateArguments(metadata.arguments() == null ? null : metadata.arguments().path("game"), variables);
	}

	/**
	 * Builds the JVM argument list for the dedicated server baseline.
	 *
	 * <p>Mojang's version metadata only exposes the client-specific `jvm` argument set. The server
	 * baseline therefore keeps the shared `default-user-jvm` arguments and lets the Fabric wrapper
	 * add the dedicated-server bootstrap properties separately.
	 *
	 * @param metadata  the resolved Mojang version metadata
	 * @param variables the argument substitution variables
	 *
	 * @return the resolved JVM arguments
	 */
	private List<String> buildServerJvmArgs(MojangVersionMetadata metadata, Map<String, String> variables)
	{
		return evaluateArguments(
				metadata.arguments() == null ? null : metadata.arguments().path("default-user-jvm"),
				variables
		);
	}

	/**
	 * Builds the full runtime classpath for a selected Mojang version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param metadata  the resolved version metadata
	 *
	 * @return the ordered runtime classpath
	 */
	private List<Path> buildClasspath(
			String versionId,
			MojangVersionMetadata metadata,
			LaunchEnvironment environment,
			List<Path> bundledServerLibraries
	)
	{
		Set<Path> classpath = new LinkedHashSet<>();

		for (var library : metadata.libraries())
		{
			if (!_mojangClient.isLibraryAllowed(library))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null || library.downloads().artifact().path() == null)
			{
				continue;
			}

			classpath.add(ToolchainPaths.mojangLibraryFile(library.downloads().artifact().path()));
		}

		if (environment.isServer())
		{
			classpath.addAll(bundledServerLibraries);
		}

		classpath.add(
				environment.isClient()
				? ToolchainPaths.mojangClientJarFile(versionId)
				: ToolchainPaths.mojangExtractedServerJarFile(versionId)
		);
		return new ArrayList<>(classpath);
	}

	/**
	 * Extracts native libraries from downloaded native jars into the chosen natives directory.
	 *
	 * @param metadata         the resolved version metadata
	 * @param nativesDirectory the target natives directory
	 *
	 * @throws IOException if extraction fails
	 */
	private void extractNativeLibraries(MojangVersionMetadata metadata, Path nativesDirectory) throws IOException
	{
		Files.createDirectories(nativesDirectory);
		Set<Path> nativeJars = new LinkedHashSet<>();

		for (var library : metadata.libraries())
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

			nativeJars.add(ToolchainPaths.mojangLibraryFile(library.downloads().artifact().path()));
		}

		for (var nativeJar : nativeJars)
		{
			try (var inputStream = Files.newInputStream(nativeJar);
			     var zipInputStream = new ZipInputStream(inputStream))
			{
				ZipEntry entry;

				while ((entry = zipInputStream.getNextEntry()) != null)
				{
					if (entry.isDirectory())
					{
						continue;
					}

					var fileName = Path.of(entry.getName()).getFileName().toString();

					if (!isNativeLibrary(fileName))
					{
						continue;
					}

					var target = nativesDirectory.resolve(fileName);

					try (var outputStream = Files.newOutputStream(
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
	 * @param refresh  whether to force a fresh download
	 *
	 * @return the cached logging configuration path, or {@code null}
	 *
	 * @throws IOException if the logging configuration cannot be downloaded
	 */
	private Path prepareLoggingConfiguration(
			Path instanceRoot,
			Path gameDirectory,
			MojangVersionMetadata metadata,
			boolean refresh
	) throws IOException
	{
		var generatedConfiguration = instanceRoot.resolve("config").resolve("log4j2-intellij.xml");
		Files.createDirectories(generatedConfiguration.getParent());
		var latestLog = xmlPath(gameDirectory.resolve("logs").resolve("latest.log"));
		var archivedLogs = xmlPath(gameDirectory.resolve("logs").resolve("%d{yyyy-MM-dd}-%i.log.gz"));
		var debugLog = xmlPath(gameDirectory.resolve("logs").resolve("debug.log"));
		var debugArchivedLogs = xmlPath(gameDirectory.resolve("logs").resolve("debug-%i.log.gz"));
		Map<String, String> templateValues = new LinkedHashMap<>();
		templateValues.put("LATEST_LOG", latestLog);
		templateValues.put("ARCHIVED_LOGS", archivedLogs);
		templateValues.put("DEBUG_LOG", debugLog);
		templateValues.put("DEBUG_ARCHIVED_LOGS", debugArchivedLogs);
		var xml = FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/log4j2-intellij.xml",
				templateValues
		);
		Files.writeString(generatedConfiguration, xml);

		if (metadata.logging() != null && metadata.logging().client() != null && metadata.logging().client().file() != null)
		{
			var target = ToolchainPaths.MOJANG_LOGGING_ROOT.resolve(metadata.logging().client().file().id());
			_mojangClient.download(URI.create(metadata.logging().client().file().url()), target, refresh);
		}

		return generatedConfiguration;
	}

	/**
	 * Builds the substitution variables used by Mojang argument templates.
	 *
	 * @param versionId            the Minecraft version identifier
	 * @param metadata             the resolved version metadata
	 * @param gameDirectory        the game directory
	 * @param nativesDirectory     the natives directory
	 * @param classpath            the resolved classpath
	 * @param loggingConfiguration the optional logging configuration path
	 *
	 * @return the resolved variable map
	 */
	private Map<String, String> buildLaunchVariables(
			String versionId,
			MojangVersionMetadata metadata,
			Path gameDirectory,
			Path nativesDirectory,
			List<Path> classpath,
			Path loggingConfiguration,
			LaunchIdentity identity
	)
	{
		Map<String, String> variables = new HashMap<>();
		var classpathSeparator = File.pathSeparator;

		variables.put("auth_player_name", identity.username());
		variables.put("version_name", versionId);
		variables.put("game_directory", gameDirectory.toAbsolutePath().toString());
		variables.put("assets_root", ToolchainPaths.MOJANG_ASSETS_ROOT.toAbsolutePath().toString());
		variables.put("assets_index_name", metadata.assetIndex().id());
		variables.put("auth_uuid", identity.uuid());
		variables.put("auth_access_token", "0");
		variables.put("clientid", "0");
		variables.put("auth_xuid", "0");
		variables.put("version_type", metadata.id());
		variables.put("launcher_name", DEFAULT_LAUNCHER_NAME);
		variables.put("launcher_version", DEFAULT_LAUNCHER_VERSION);
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
	 * @param variables     the substitution variables
	 *
	 * @return the resolved argument list
	 */
	private List<String> evaluateArguments(JsonNode argumentsNode, Map<String, String> variables)
	{
		List<String> resolved = new ArrayList<>();

		if (argumentsNode == null || argumentsNode.isMissingNode() || argumentsNode.getNodeType() != JsonNodeType.ARRAY)
		{
			return resolved;
		}

		var arrayNode = (ArrayNode)argumentsNode;

		for (var entry : arrayNode)
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

			var valueNode = entry.path("value");

			if (valueNode.isTextual())
			{
				resolved.add(substitute(valueNode.asText(), variables));
				continue;
			}

			if (valueNode.isArray())
			{
				for (var valueEntry : valueNode)
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
	 *
	 * @return {@code true} if the entry should be included
	 */
	private boolean isAllowed(JsonNode rulesNode)
	{
		if (rulesNode == null || rulesNode.isMissingNode() || !rulesNode.isArray() || rulesNode.isEmpty())
		{
			return true;
		}

		var allowed = false;

		for (var ruleNode : rulesNode)
		{
			if (!ruleNode.isObject())
			{
				continue;
			}

			if (ruleNode.hasNonNull("features"))
			{
				continue;
			}

			var rule = _mapper.convertValue(ruleNode, MojangRule.class);

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
	 * @param value     the raw template string
	 * @param variables the variable map
	 *
	 * @return the substituted value
	 */
	private String substitute(String value, Map<String, String> variables)
	{
		var substituted = value;

		for (var entry : variables.entrySet())
		{
			substituted = substituted.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		return substituted;
	}

	/**
	 * Derived path layout for a shared Mojang client runtime baseline.
	 *
	 * @param instanceRoot     the shared runtime instance root
	 * @param gameDirectory    the game directory
	 * @param nativesDirectory the natives extraction directory
	 */
	private record VanillaLaunchPaths(
			Path instanceRoot,
			Path gameDirectory,
			Path nativesDirectory
	)
	{
	}

	/**
	 * Resolves the Java executable path for child launch processes.
	 *
	 * @return the Java executable path
	 */
	private String findJavaExecutable()
	{
		return Path.of(System.getProperty("java.home"), "bin", HostPlatform.current().isWindows() ? "java.exe" : "java").toString();
	}

	/**
	 * Escapes a filesystem path for safe use in XML attributes.
	 *
	 * @param path the path to escape
	 *
	 * @return the escaped path string
	 */
	private String xmlPath(Path path)
	{
		return XmlEscaper.escapePath(path);
	}

	/**
	 * Checks whether a file is a native library candidate.
	 *
	 * @param fileName the file name to inspect
	 *
	 * @return {@code true} if the file should be extracted as a native library
	 */
	private boolean isNativeLibrary(String fileName)
	{
		var lower = fileName.toLowerCase(Locale.ROOT);
		return lower.endsWith(".dll") || lower.endsWith(".so") || lower.endsWith(".dylib") || lower.endsWith(".jnilib");
	}
}
