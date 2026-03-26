package com.parzivail.toolchain.path;

import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.util.HostPlatform;
import com.parzivail.toolchain.util.ToolchainLog;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Common paths used by the toolchain.
 */
public class ToolchainPaths
{
	/**
	 * Discovers the tracked host-project repository root from the current working directory.
	 *
	 * <p>The standalone toolchain runs both from its own project root and from the tracked host
	 * project. Walking upward keeps repository discovery stable in both modes.
	 *
	 * @return the discovered repository root
	 */
	private static Path discoverProjectRoot()
	{
		var workingDirectory = ToolchainPaths.getWorkingDirectory();
		for (var candidate = workingDirectory; candidate != null; candidate = candidate.getParent())
		{
			// If we don't see a toolchain file, keep moving up
			if (!Files.isRegularFile(candidate.resolve(CONFIG_FILE_NAME)))
			{
				continue;
			}

			// If we do see a toolchain file, and have the toolchain directory, we're done
			if (Files.isDirectory(candidate.resolve(TOOLCHAIN_FOLDER_NAME)))
			{
				return candidate;
			}
		}

		ToolchainLog.info("error", "Could not find the project's toolchain.toml file.");
		System.exit(-1);
		return null;
	}

	/**
	 * The project root.
	 */
	public static final Path PROJECT_ROOT = discoverProjectRoot();

	/**
	 * The toolchain root directory name.
	 */
	public static final String TOOLCHAIN_FOLDER_NAME = "toolchain";

	/**
	 * The toolchain root.
	 */
	public static final Path TOOLCHAIN_ROOT = PROJECT_ROOT.resolve(TOOLCHAIN_FOLDER_NAME);

	/**
	 * The toolchain source directory.
	 */
	public static final Path TOOLCHAIN_MAIN_SOURCES = TOOLCHAIN_ROOT.resolve("src", "main", "java");

	/**
	 * The toolchain resource directory.
	 */
	public static final Path TOOLCHAIN_MAIN_RESOURCES = TOOLCHAIN_ROOT.resolve("src", "main", "resources");

	/**
	 * The tracked configuration file name.
	 */
	public static final String CONFIG_FILE_NAME = "toolchain.toml";

	/**
	 * The tracked configuration file.
	 */
	public static final Path CONFIG_FILE = ToolchainPaths.PROJECT_ROOT.resolve(CONFIG_FILE_NAME);

	/**
	 * The work root.
	 */
	public static final Path WORK_ROOT = TOOLCHAIN_ROOT.resolve("work");

	/**
	 * The output root used for artifacts.
	 */
	public static final Path ARTIFACTS_ROOT = WORK_ROOT.resolve("artifacts");

	/**
	 * The output root used for game instances.
	 */
	public static final Path INSTANCE_ROOT = WORK_ROOT.resolve("instances");

	/**
	 * The output root used for game instance configs.
	 */
	public static final String INSTANCE_CONFIG_ROOT_NAME = "config";

	/**
	 * The output root used for game instance games.
	 */
	public static final String INSTANCE_GAME_ROOT_NAME = "game";

	/**
	 * The output root used for Fabric datagen instances.
	 */
	public static final Path FABRIC_DATAGEN_INSTANCE_ROOT = INSTANCE_ROOT.resolve("fabric-datagen");

	/**
	 * The output root used for CI compilation.
	 */
	public static final Path CI_OUTPUT_ROOT = WORK_ROOT.resolve("ci-build", "out", "production");

	/**
	 * The root used for cached assets.
	 */
	public static final Path CACHE_ROOT = WORK_ROOT.resolve("cache");

	/**
	 * The root used for cached Maven data.
	 */
	public static final Path MAVEN_CACHE_ROOT = CACHE_ROOT.resolve("maven");

	/**
	 * The root used for cached Mojang data.
	 */
	public static final Path MOJANG_CACHE_ROOT = CACHE_ROOT.resolve("mojang");

	/**
	 * The root used for cached Parchment data.
	 */
	public static final Path PARCHMENT_CACHE_ROOT = CACHE_ROOT.resolve("parchment");

	/**
	 * The Mojang version manifest file.
	 */
	public static final Path MOJANG_VERSION_MANIFEST_FILE = ToolchainPaths.MOJANG_CACHE_ROOT.resolve("version_manifest_v2.json");

	/**
	 * The root used for Mojang versions.
	 */
	public static final Path MOJANG_VERSIONS_ROOT = MOJANG_CACHE_ROOT.resolve("versions");

	/**
	 * The root used for Mojang libraries.
	 */
	public static final Path MOJANG_LIBRARIES_ROOT = MOJANG_CACHE_ROOT.resolve("libraries");

	/**
	 * The root used for Mojang logging configs.
	 */
	public static final Path MOJANG_LOGGING_ROOT = MOJANG_CACHE_ROOT.resolve("logging");

	/**
	 * The root used for Mojang assets.
	 */
	public static final Path MOJANG_ASSETS_ROOT = MOJANG_CACHE_ROOT.resolve("assets");

	/**
	 * The root used for Mojang asset indices.
	 */
	public static final Path MOJANG_ASSET_INDICES_ROOT = MOJANG_ASSETS_ROOT.resolve("indexes");

	/**
	 * The root used for Mojang asset objects.
	 */
	public static final Path MOJANG_ASSET_OBJECTS_ROOT = MOJANG_ASSETS_ROOT.resolve("objects");

	/**
	 * The IntelliJ output directory segment used for compiled module outputs.
	 */
	public static final Path INTELLIJ_OUTPUT_DIRECTORY = PROJECT_ROOT.resolve("out", "production");

	/**
	 * The IntelliJ project metadata directory.
	 */
	public static final Path INTELLIJ_META_DIRECTORY = PROJECT_ROOT.resolve(".idea");

	/**
	 * The IntelliJ project metadata directory.
	 */
	public static final Path INTELLIJ_RUN_CONFIGS_DIRECTORY = INTELLIJ_META_DIRECTORY.resolve("runConfigurations");

	/**
	 * The IntelliJ project metadata directory.
	 */
	public static final Path INTELLIJ_META_MODULES_DIRECTORY = INTELLIJ_META_DIRECTORY.resolve("modules");

	/**
	 * The IntelliJ projects module metadata directory.
	 */
	public static final Path INTELLIJ_META_PROJECTS_MODULE_DIRECTORY = INTELLIJ_META_MODULES_DIRECTORY.resolve("projects");

	/**
	 * The IntelliJ toolchain project module metadata directory.
	 */
	public static final Path INTELLIJ_META_TOOLCHAIN_PROJECT_MODULE_DIRECTORY = INTELLIJ_META_PROJECTS_MODULE_DIRECTORY.resolve("toolchain");

	/**
	 * The IntelliJ project modules metadata file.
	 */
	public static final Path INTELLIJ_META_MODULES_FILE = INTELLIJ_META_DIRECTORY.resolve("modules.xml");

	/**
	 * The IntelliJ project misc metadata file.
	 */
	public static final Path INTELLIJ_META_MISC_FILE = INTELLIJ_META_DIRECTORY.resolve("misc.xml");

	/**
	 * The IntelliJ project compiler metadata file.
	 */
	public static final Path INTELLIJ_META_COMPILER_FILE = INTELLIJ_META_DIRECTORY.resolve("compiler.xml");

	/**
	 * The IntelliJ project libraries directory.
	 */
	public static final Path INTELLIJ_META_LIBRARIES_DIRECTORY = INTELLIJ_META_DIRECTORY.resolve("libraries");

	/**
	 * The IntelliJ project metadata directory.
	 */
	public static final Path INTELLIJ_LAUNCH_MODULE_DIRECTORY = INTELLIJ_META_MODULES_DIRECTORY.resolve("launch");

	/**
	 * The IntelliJ project metadata directory.
	 */
	public static final Path INTELLIJ_FABRIC_LAUNCH_MODULE_DIRECTORY = INTELLIJ_LAUNCH_MODULE_DIRECTORY.resolve("fabric");

	/**
	 * Gets the instance root for the given environment and platform.
	 *
	 * @param versionId   the Minecraft version identifier
	 * @param environment the launch environment
	 * @param platform    the target platform identifier
	 *
	 * @return the instance root
	 */
	public static Path getInstanceRoot(String versionId, LaunchEnvironment environment, HostPlatform platform)
	{
		return INSTANCE_ROOT.resolve(environment.fabricInstanceDirectoryName())
		                    .resolve(platform.id())
		                    .resolve(versionId);
	}

	/**
	 * Gets the datagen instance root for the given environment and platform.
	 *
	 * @param versionId the Minecraft version identifier
	 * @param platform  the target platform identifier
	 *
	 * @return the instance root
	 */
	public static Path getDatagenInstanceRoot(String versionId, HostPlatform platform)
	{
		return FABRIC_DATAGEN_INSTANCE_ROOT.resolve(platform.id())
		                                   .resolve(versionId);
	}

	/**
	 * Gets the toolchain working directory.
	 *
	 * @return the working directory
	 */
	public static Path getWorkingDirectory()
	{
		return Path.of("").toAbsolutePath().normalize();
	}

	/**
	 * Gets the cached version metadata file path for a specific Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the cached version metadata file path
	 */
	public static Path mojangVersionMetadataFile(String versionId)
	{
		return ToolchainPaths.MOJANG_VERSIONS_ROOT.resolve(versionId + ".json");
	}

	/**
	 * Gets the cached vanilla client jar path for a specific Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the cached client jar path
	 */
	public static Path mojangClientJarFile(String versionId)
	{
		return ToolchainPaths.MOJANG_VERSIONS_ROOT.resolve(versionId).resolve("client.jar");
	}

	/**
	 * Gets the cached vanilla server bootstrap jar path for a specific Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the cached server bootstrap jar path
	 */
	public static Path mojangServerJarFile(String versionId)
	{
		return ToolchainPaths.MOJANG_VERSIONS_ROOT.resolve(versionId).resolve("server.jar");
	}

	/**
	 * Gets the cached extracted vanilla server jar path for a specific Minecraft version.
	 *
	 * <p>Modern versions may ship the server as a bootstrap bundle containing the actual runnable jar
	 * under `META-INF/versions`. The toolchain extracts that nested jar here so common/main source
	 * sets can compile against the real dedicated-server classes.
	 *
	 * @param versionId the Minecraft version identifier
	 *
	 * @return the cached extracted server jar path
	 */
	public static Path mojangExtractedServerJarFile(String versionId)
	{
		return ToolchainPaths.MOJANG_VERSIONS_ROOT.resolve(versionId).resolve("server-extracted.jar");
	}

	/**
	 * Gets the cached asset index path for a specific asset index identifier.
	 *
	 * @param assetIndexId the asset index identifier
	 *
	 * @return the cached asset index path
	 */
	public static Path mojangAssetIndexFile(String assetIndexId)
	{
		return ToolchainPaths.MOJANG_ASSET_INDICES_ROOT.resolve(assetIndexId + ".json");
	}

	/**
	 * Gets the cached path for a Mojang library artifact.
	 *
	 * @param artifactPath the relative library artifact path
	 *
	 * @return the cached library file path
	 */
	public static Path mojangLibraryFile(String artifactPath)
	{
		return ToolchainPaths.MOJANG_LIBRARIES_ROOT.resolve(artifactPath);
	}

	/**
	 * Gets the cached asset object path for a specific object hash.
	 *
	 * @param hash the asset object hash
	 *
	 * @return the cached asset object path
	 */
	public static Path mojangAssetObjectFile(String hash)
	{
		var prefix = hash.substring(0, 2);
		return ToolchainPaths.MOJANG_ASSET_OBJECTS_ROOT.resolve(prefix).resolve(hash);
	}
}
