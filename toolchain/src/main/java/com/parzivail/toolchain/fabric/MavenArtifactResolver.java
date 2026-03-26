package com.parzivail.toolchain.fabric;

import com.parzivail.toolchain.mojang.MojangMetadataClient;
import com.parzivail.toolchain.path.ToolchainPaths;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

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
	 * Creates a new resolver rooted at the toolchain work cache.
	 */
	public MavenArtifactResolver()
	{
		_downloadClient = new MojangMetadataClient();
	}

	/**
	 * Resolves a Maven artifact into the local cache.
	 *
	 * @param coordinate    the artifact coordinate
	 * @param repositoryUri the Maven repository base URI
	 * @param refresh       whether to force a fresh download
	 *
	 * @return the cached artifact path
	 *
	 * @throws IOException if the artifact cannot be downloaded
	 */
	public Path resolve(MavenCoordinate coordinate, URI repositoryUri, boolean refresh) throws IOException
	{
		return resolve(coordinate, repositoryUri, null, refresh);
	}

	/**
	 * Resolves a specific classifier variant of a Maven artifact into the local cache.
	 *
	 * @param coordinate    the artifact coordinate
	 * @param repositoryUri the Maven repository base URI
	 * @param classifier    the optional classifier
	 * @param refresh       whether to force a fresh download
	 *
	 * @return the cached artifact path
	 *
	 * @throws IOException if the artifact cannot be downloaded
	 */
	public Path resolve(
			MavenCoordinate coordinate,
			URI repositoryUri,
			String classifier,
			boolean refresh
	) throws IOException
	{
		var repositoryPath = coordinate.repositoryPath(classifier);
		var target = ToolchainPaths.MAVEN_CACHE_ROOT.resolve(repositoryPath);
		_downloadClient.download(artifactUri(repositoryUri, repositoryPath), target, refresh);
		return target;
	}

	/**
	 * Resolves an optional classifier artifact from a list of candidate repositories.
	 *
	 * @param coordinate     the artifact coordinate
	 * @param repositoryUris the candidate repositories
	 * @param classifier     the optional classifier
	 * @param refresh        whether to force a fresh download
	 *
	 * @return the cached artifact path, or {@code null} when no repository serves it
	 *
	 * @throws IOException if all resolution attempts fail for non-404 reasons
	 */
	public Path resolveOptional(
			MavenCoordinate coordinate,
			List<URI> repositoryUris,
			String classifier,
			boolean refresh
	) throws IOException
	{
		IOException lastFailure = null;

		for (var repositoryUri : repositoryUris)
		{
			try
			{
				return resolve(coordinate, repositoryUri, classifier, refresh);
			}
			catch (IOException exception)
			{
				lastFailure = exception;
			}
		}

		if (lastFailure == null)
		{
			return null;
		}

		if (isMissingArtifact(lastFailure))
		{
			return null;
		}

		throw lastFailure;
	}

	/**
	 * Builds an artifact URI below a Maven repository root.
	 *
	 * @param repositoryUri  the repository base URI
	 * @param repositoryPath the artifact path
	 *
	 * @return the artifact URI
	 */
	private static URI artifactUri(URI repositoryUri, String repositoryPath)
	{
		var repositoryRoot = repositoryUri.toString();

		if (!repositoryRoot.endsWith("/"))
		{
			repositoryRoot += "/";
		}

		return URI.create(repositoryRoot + repositoryPath);
	}

	/**
	 * Returns whether an exception chain represents a missing optional artifact.
	 *
	 * @param exception the resolution failure
	 *
	 * @return {@code true} when the artifact is absent from the repository
	 */
	private static boolean isMissingArtifact(IOException exception)
	{
		for (Throwable cause = exception; cause != null; cause = cause.getCause())
		{
			var message = cause.getMessage();

			if (message != null && (message.contains("HTTP 404") || message.contains("HTTP 403")))
			{
				return true;
			}
		}

		return false;
	}
}
