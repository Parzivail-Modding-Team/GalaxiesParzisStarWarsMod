package dev.pswg.toolchain.mojang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides the standard cache layout for Mojang metadata within the toolchain work directory.
 */
public final class MojangPaths
{
	/**
	 * The standalone toolchain directory name.
	 */
	private static final String TOOLCHAIN_DIRECTORY = "toolchain";

	/**
	 * The toolchain work directory root.
	 */
	private final Path _workRoot;

	/**
	 * The Mojang cache root.
	 */
	private final Path _mojangRoot;

	/**
	 * Creates the standard Mojang cache path helper.
	 */
	public MojangPaths()
	{
		_workRoot = discoverToolchainRoot().resolve("work");
		_mojangRoot = _workRoot.resolve("cache").resolve("mojang");
	}

	/**
	 * Discovers the standalone toolchain root from either the toolchain project directory or the PSWG
	 * repository root.
	 *
	 * @return the resolved toolchain root
	 */
	private static Path discoverToolchainRoot()
	{
		Path workingDirectory = Path.of("").toAbsolutePath().normalize();

		for (Path candidate = workingDirectory; candidate != null; candidate = candidate.getParent())
		{
			if (TOOLCHAIN_DIRECTORY.equals(candidate.getFileName() == null ? null : candidate.getFileName().toString()))
			{
				return candidate;
			}

			Path nestedToolchain = candidate.resolve(TOOLCHAIN_DIRECTORY);

			if (Files.isDirectory(nestedToolchain))
			{
				return nestedToolchain;
			}
		}

		throw new IllegalStateException("Could not discover toolchain root from " + workingDirectory);
	}

	/**
	 * Gets the toolchain work root.
	 *
	 * @return the work root
	 */
	public Path workRoot()
	{
		return _workRoot;
	}

	/**
	 * Gets the Mojang cache root.
	 *
	 * @return the Mojang cache root
	 */
	public Path mojangRoot()
	{
		return _mojangRoot;
	}

	/**
	 * Gets the cached version manifest file path.
	 *
	 * @return the cached version manifest file path
	 */
	public Path versionManifestFile()
	{
		return _mojangRoot.resolve("version_manifest_v2.json");
	}

	/**
	 * Gets the cached version metadata file path for a specific Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @return the cached version metadata file path
	 */
	public Path versionMetadataFile(String versionId)
	{
		return _mojangRoot.resolve("versions").resolve(versionId + ".json");
	}

	/**
	 * Gets the cached vanilla client jar path for a specific Minecraft version.
	 *
	 * @param versionId the Minecraft version identifier
	 * @return the cached client jar path
	 */
	public Path clientJarFile(String versionId)
	{
		return _mojangRoot.resolve("versions").resolve(versionId).resolve("client.jar");
	}

	/**
	 * Gets the cached asset index path for a specific asset index identifier.
	 *
	 * @param assetIndexId the asset index identifier
	 * @return the cached asset index path
	 */
	public Path assetIndexFile(String assetIndexId)
	{
		return _mojangRoot.resolve("assets").resolve("indexes").resolve(assetIndexId + ".json");
	}

	/**
	 * Gets the cached runtime libraries root.
	 *
	 * @return the runtime libraries root
	 */
	public Path librariesRoot()
	{
		return _mojangRoot.resolve("libraries");
	}

	/**
	 * Gets the cached asset objects root.
	 *
	 * @return the asset objects root
	 */
	public Path assetObjectsRoot()
	{
		return _mojangRoot.resolve("assets").resolve("objects");
	}

	/**
	 * Gets the cached path for a Mojang library artifact.
	 *
	 * @param artifactPath the relative library artifact path
	 * @return the cached library file path
	 */
	public Path libraryFile(String artifactPath)
	{
		return librariesRoot().resolve(artifactPath);
	}

	/**
	 * Gets the cached asset object path for a specific object hash.
	 *
	 * @param hash the asset object hash
	 * @return the cached asset object path
	 */
	public Path assetObjectFile(String hash)
	{
		String prefix = hash.substring(0, 2);
		return assetObjectsRoot().resolve(prefix).resolve(hash);
	}
}
