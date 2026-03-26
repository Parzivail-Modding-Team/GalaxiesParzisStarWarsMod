package com.parzivail.toolchain.fabric;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parzivail.toolchain.intellij.IntelliJRunConfigurationSupport;
import com.parzivail.toolchain.model.MavenDependencySpec;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.runtime.VanillaLaunchConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared helpers for Fabric launch-bundle preparation across development and datagen workflows.
 */
public final class FabricLaunchSupport
{
	/**
	 * Prevents construction.
	 */
	private FabricLaunchSupport()
	{
	}

	/**
	 * Builds the classpath entries that must appear ahead of the vanilla runtime.
	 *
	 * @param moduleRoots              the injected module roots
	 * @param dependencyRoots          the modeled dependency roots
	 * @param externalRuntimeArtifacts the resolved external runtime artifacts
	 * @param runtimeClasspath         the resolved Fabric runtime classpath
	 *
	 * @return the ordered prepended classpath entries
	 */
	public static List<Path> buildPrependedClasspath(
			List<Path> moduleRoots,
			List<Path> dependencyRoots,
			List<Path> externalRuntimeArtifacts,
			List<Path> runtimeClasspath
	)
	{
		List<Path> prependedClasspath = new ArrayList<>(moduleRoots);
		prependedClasspath.addAll(dependencyRoots);
		prependedClasspath.addAll(externalRuntimeArtifacts);
		prependedClasspath.addAll(runtimeClasspath);
		return prependedClasspath;
	}

	/**
	 * Replaces the existing launch classpath with a Fabric-prepended classpath.
	 *
	 * @param jvmArgs            the JVM argument list to update
	 * @param prependedClasspath the classpath entries to prepend
	 */
	public static void replaceClasspath(List<String> jvmArgs, List<Path> prependedClasspath)
	{
		var separator = File.pathSeparator;
		var prependedValue = prependedClasspath.stream()
		                                       .map(path -> path.toAbsolutePath().toString())
		                                       .reduce((left, right) -> left + separator + right)
		                                       .orElse("");

		for (var index = 0; index < jvmArgs.size() - 1; index++)
		{
			var argument = jvmArgs.get(index);

			if ("-cp".equals(argument) || "-classpath".equals(argument))
			{
				jvmArgs.set(index + 1, prependedValue + separator + jvmArgs.get(index + 1));
				return;
			}
		}

		if (!prependedValue.isBlank())
		{
			jvmArgs.add("-cp");
			jvmArgs.add(prependedValue);
		}
	}

	/**
	 * Replaces the logging configuration path inherited from the vanilla baseline.
	 *
	 * @param jvmArgs            the JVM argument list
	 * @param loggingConfigPath  the generated Fabric logging configuration path
	 * @param ansiLoggingEnabled whether ANSI logging should remain enabled
	 */
	public static void replaceLoggingConfiguration(
			List<String> jvmArgs,
			Path loggingConfigPath,
			boolean ansiLoggingEnabled
	)
	{
		jvmArgs.removeIf(argument -> argument.startsWith("-Dlog4j.configurationFile="));
		jvmArgs.add("-Dlog4j.configurationFile=" + loggingConfigPath.toAbsolutePath());
		jvmArgs.add("-Dlog4j2.formatMsgNoLookups=true");
		jvmArgs.add("-Dfabric.log.disableAnsi=" + !ansiLoggingEnabled);
	}

	/**
	 * Adds conservative JVM compatibility flags used by modern Minecraft launches.
	 *
	 * @param jvmArgs the JVM argument list
	 */
	public static void addHostCompatibilityFlags(List<String> jvmArgs)
	{
		addIfMissing(jvmArgs, "--sun-misc-unsafe-memory-access=allow");
		addIfMissing(jvmArgs, "--enable-native-access=ALL-UNNAMED");
	}

	/**
	 * Adds the optional Mixin javaagent when the resolved runtime requires it.
	 *
	 * @param jvmArgs          the JVM argument list
	 * @param runtimeArtifacts the resolved Fabric runtime artifacts
	 */
	public static void addMixinJavaAgent(
			List<String> jvmArgs,
			FabricRuntimeArtifacts runtimeArtifacts
	)
	{
		if (runtimeArtifacts.mixinJavaAgentJar() != null)
		{
			addIfMissing(jvmArgs, "-javaagent:" + runtimeArtifacts.mixinJavaAgentJar().toAbsolutePath());
		}
	}

	/**
	 * Creates the Fabric-style asset index alias expected by the dev launcher.
	 *
	 * @param versionId    the Minecraft version identifier
	 * @param assetIndexId the Mojang asset index identifier
	 *
	 * @throws IOException if the alias cannot be created
	 */
	public static void prepareFabricAssetIndex(
			String versionId,
			String assetIndexId
	) throws IOException
	{
		var source = ToolchainPaths.mojangAssetIndexFile(assetIndexId);
		var target = ToolchainPaths.mojangAssetIndexFile(versionId + "-" + assetIndexId);

		Files.createDirectories(target.getParent());
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Writes the Loom-style IntelliJ log4j configuration for one Fabric launch bundle.
	 *
	 * @param vanillaLaunch the prepared vanilla launch configuration
	 * @param outputPath    the target configuration path
	 *
	 * @throws IOException if the file cannot be written
	 */
	public static void writeLoggingConfig(
			VanillaLaunchConfig vanillaLaunch,
			Path outputPath
	) throws IOException
	{
		Map<String, String> values = new LinkedHashMap<>();

		var logsPath = vanillaLaunch.gameDirectory().resolve("logs");

		values.put("LATEST_LOG", IntelliJRunConfigurationSupport.xmlPath(logsPath.resolve("latest.log")));
		values.put("ARCHIVED_LOGS", IntelliJRunConfigurationSupport.xmlPath(logsPath.resolve("%d{yyyy-MM-dd}-%i.log.gz")));
		values.put("DEBUG_LOG", IntelliJRunConfigurationSupport.xmlPath(logsPath.resolve("debug.log")));
		values.put("DEBUG_ARCHIVED_LOGS", IntelliJRunConfigurationSupport.xmlPath(logsPath.resolve("debug-%i.log.gz")));
		var rendered = com.parzivail.toolchain.template.FileTemplateRenderer.render(
				"com/parzivail/toolchain/templates/log4j2-intellij.xml",
				values
		);

		Files.createDirectories(outputPath.getParent());
		Files.writeString(outputPath, rendered);
	}

	/**
	 * Writes the serialized bootstrap launch JSON.
	 *
	 * @param mapper     the shared JSON serializer
	 * @param outputPath the target launch JSON path
	 * @param config     the launch configuration
	 *
	 * @throws IOException if the file cannot be written
	 */
	public static void writeLaunchJson(
			ObjectMapper mapper,
			Path outputPath,
			VanillaLaunchConfig config
	) throws IOException
	{
		Files.createDirectories(outputPath.getParent());
		mapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), config);
	}

	/**
	 * Renders optional shared Fabric launcher properties for injected grouped module roots.
	 *
	 * @param moduleRoots the injected module classpath roots
	 *
	 * @return the rendered optional property lines, each ending in a newline
	 */
	public static String optionalCommonProperties(List<Path> moduleRoots)
	{
		if (moduleRoots.size() < 2)
		{
			return "";
		}

		var joinedRoots = moduleRoots.stream()
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
	 * Renders Loom-style common game-jar properties for split source-set launches.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the rendered common property lines, each ending in a newline
	 */
	public static String environmentCommonProperties(String versionId)
	{
		return "\tfabric.gameJarPath=" + ToolchainPaths.mojangExtractedServerJarFile(versionId).toAbsolutePath() + "\n";
	}

	/**
	 * Renders the client-specific Fabric dev-launch property section.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the rendered section text
	 */
	public static String clientPropertiesSection(String versionId)
	{
		return "clientProperties\n"
		       + "\tfabric.gameJarPath.client=" + ToolchainPaths.mojangClientJarFile(versionId).toAbsolutePath() + "\n";
	}

	/**
	 * Renders the client-specific Fabric dev-launch argument section.
	 *
	 * @param versionId     the Minecraft version identifier
	 * @param vanillaLaunch the prepared vanilla launch baseline
	 *
	 * @return the rendered client section text
	 */
	public static String clientArgsSection(
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
	 * Resolves declared module runtime Maven dependencies into concrete jars.
	 *
	 * @param runtimeResolver     the Fabric runtime resolver
	 * @param runtimeDependencies the declared runtime dependencies
	 * @param refresh             whether to revalidate cached dependency downloads
	 *
	 * @return the resolved runtime dependency jars
	 *
	 * @throws IOException if any dependency cannot be resolved
	 */
	public static List<Path> resolveRuntimeDependencies(
			FabricRuntimeResolver runtimeResolver,
			List<MavenDependencySpec> runtimeDependencies,
			boolean refresh
	) throws IOException
	{
		List<Path> artifacts = new ArrayList<>();

		for (var runtimeDependency : runtimeDependencies)
		{
			var artifact = runtimeResolver.resolveRuntimeDependency(runtimeDependency, refresh);

			if (!artifacts.contains(artifact))
			{
				artifacts.add(artifact);
			}
		}

		return artifacts;
	}

	/**
	 * Adds one JVM argument only when it is not already present.
	 *
	 * @param arguments the JVM argument list
	 * @param argument  the argument to add
	 */
	private static void addIfMissing(
			List<String> arguments,
			String argument
	)
	{
		if (!arguments.contains(argument))
		{
			arguments.add(argument);
		}
	}
}
