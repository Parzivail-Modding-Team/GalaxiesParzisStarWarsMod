package dev.pswg.toolchain.mojang;

import java.nio.file.Path;

/**
 * Provides the standard cache layout for Mojang metadata within the toolchain work directory.
 */
public final class MojangPaths
{
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
		_workRoot = Path.of("work");
		_mojangRoot = _workRoot.resolve("cache").resolve("mojang");
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
}
