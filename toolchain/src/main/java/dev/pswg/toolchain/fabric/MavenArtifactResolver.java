package dev.pswg.toolchain.fabric;

import dev.pswg.toolchain.mojang.MojangMetadataClient;
import dev.pswg.toolchain.mojang.MojangPaths;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

/**
 * Resolves Maven artifacts into the toolchain cache.
 */
public final class MavenArtifactResolver
{
	/**
	 * The shared download client.
	 */
	private final MojangMetadataClient _downloadClient;

	/**
	 * The local Maven-style cache root.
	 */
	private final Path _cacheRoot;

	/**
	 * Creates a new resolver rooted at the toolchain work cache.
	 */
	public MavenArtifactResolver()
	{
		_downloadClient = new MojangMetadataClient();
		_cacheRoot = new MojangPaths().workRoot().resolve("cache").resolve("maven");
	}

	/**
	 * Resolves a Maven artifact into the local cache.
	 *
	 * @param coordinate the artifact coordinate
	 * @param repositoryUri the Maven repository base URI
	 * @param refresh whether to force a fresh download
	 * @return the cached artifact path
	 * @throws IOException if the artifact cannot be downloaded
	 */
	public Path resolve(MavenCoordinate coordinate, URI repositoryUri, boolean refresh) throws IOException
	{
		String repositoryRoot = repositoryUri.toString();

		if (!repositoryRoot.endsWith("/"))
		{
			repositoryRoot += "/";
		}

		String repositoryPath = coordinate.repositoryPath();
		Path target = _cacheRoot.resolve(repositoryPath);
		_downloadClient.download(URI.create(repositoryRoot + repositoryPath), target, refresh);
		return target;
	}
}
