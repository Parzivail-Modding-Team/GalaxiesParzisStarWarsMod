package com.parzivail.toolchain.runtime;

import java.nio.file.Path;
import java.util.List;

/**
 * Serializable launch configuration for a vanilla Minecraft client run.
 *
 * @param versionId                the Minecraft version identifier
 * @param mainClass                the vanilla main class
 * @param javaExecutable           the Java executable to run
 * @param workingDirectory         the instance working directory
 * @param gameDirectory            the Minecraft game directory
 * @param assetsRoot               the Mojang assets root
 * @param assetIndexId             the selected asset index identifier
 * @param nativesDirectory         the extracted native library directory
 * @param loggingConfigurationFile the optional logging configuration file
 * @param classpath                the complete ordered runtime classpath
 * @param jvmArgs                  the resolved JVM arguments
 * @param gameArgs                 the resolved game arguments
 */
public record VanillaLaunchConfig(
		String versionId,
		String mainClass,
		String javaExecutable,
		Path workingDirectory,
		Path gameDirectory,
		Path assetsRoot,
		String assetIndexId,
		Path nativesDirectory,
		Path loggingConfigurationFile,
		List<Path> classpath,
		List<String> jvmArgs,
		List<String> gameArgs
)
{
}
