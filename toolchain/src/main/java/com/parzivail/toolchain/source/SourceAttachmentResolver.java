package com.parzivail.toolchain.source;

import com.parzivail.toolchain.fabric.MavenArtifactResolver;
import com.parzivail.toolchain.fabric.MavenCoordinate;
import com.parzivail.toolchain.maven.ToolchainMavenRepositories;
import com.parzivail.toolchain.path.ToolchainPaths;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves source archives for IntelliJ-attached libraries.
 */
public final class SourceAttachmentResolver
{
	/**
	 * The Mojang libraries repository root.
	 */
	private static final URI MOJANG_LIBRARIES = URI.create("https://libraries.minecraft.net/");

	/**
	 * The Minecraft source archive generator.
	 */
	private final MinecraftSourcesGenerator _minecraftSourcesGenerator;

	/**
	 * The Maven artifact resolver.
	 */
	private final MavenArtifactResolver _mavenArtifactResolver;

	/**
	 * Per-run source attachment cache keyed by the binary artifact path.
	 */
	private final Map<Path, Path> _sourceArchiveCache;

	/**
	 * Creates a resolver with default collaborators.
	 */
	public SourceAttachmentResolver()
	{
		_minecraftSourcesGenerator = new MinecraftSourcesGenerator();
		_mavenArtifactResolver = new MavenArtifactResolver();
		_sourceArchiveCache = new LinkedHashMap<>();
	}

	/**
	 * Resolves a source archive for one binary artifact when available.
	 *
	 * @param artifact the binary artifact path
	 * @param refresh  whether refresh was requested
	 *
	 * @return the source archive, or {@code null} when none can be resolved
	 *
	 * @throws IOException if source generation or resolution fails
	 */
	public Path resolveSourceArchive(Path artifact, boolean refresh) throws IOException
	{
		var cached = _sourceArchiveCache.get(artifact);

		if (cached != null)
		{
			return Files.exists(cached) ? cached : null;
		}

		var sourceArchive = resolveSourceArchiveUncached(artifact, refresh);
		_sourceArchiveCache.put(artifact, sourceArchive);
		return sourceArchive;
	}

	/**
	 * Resolves a source archive without consulting the per-run cache.
	 *
	 * @param artifact the binary artifact path
	 * @param refresh  whether refresh was requested
	 *
	 * @return the source archive, or {@code null}
	 *
	 * @throws IOException if source generation or resolution fails
	 */
	private Path resolveSourceArchiveUncached(Path artifact, boolean refresh) throws IOException
	{
		if (artifact == null || Files.isDirectory(artifact))
		{
			return null;
		}

		if (artifact.getFileName().toString().endsWith("-sources.jar"))
		{
			return artifact;
		}

		var minecraftSources = _minecraftSourcesGenerator.generateSources(artifact, refresh);

		if (minecraftSources != null)
		{
			return minecraftSources;
		}

		return resolveMavenSources(artifact, refresh);
	}

	/**
	 * Resolves a Maven-style `sources` classifier artifact when the binary path can be mapped back to
	 * a repository layout.
	 *
	 * @param artifact the binary artifact path
	 * @param refresh  whether refresh was requested
	 *
	 * @return the resolved source archive, or {@code null}
	 *
	 * @throws IOException if optional source resolution fails unexpectedly
	 */
	private Path resolveMavenSources(Path artifact, boolean refresh) throws IOException
	{
		MavenCoordinate coordinate = null;
		List<URI> repositories = null;

		if (artifact.startsWith(ToolchainPaths.MAVEN_CACHE_ROOT))
		{
			var repositoryRelativePath = ToolchainPaths.MAVEN_CACHE_ROOT.relativize(artifact);
			coordinate = MavenCoordinate.parseRepositoryPath(repositoryRelativePath);
			repositories = inferredRepositories(coordinate, false);
		}
		else if (artifact.startsWith(ToolchainPaths.MOJANG_LIBRARIES_ROOT))
		{
			var repositoryRelativePath = ToolchainPaths.MOJANG_LIBRARIES_ROOT.relativize(artifact);
			coordinate = MavenCoordinate.parseRepositoryPath(repositoryRelativePath);
			repositories = inferredRepositories(coordinate, true);
		}

		if (coordinate == null || repositories == null || repositories.isEmpty())
		{
			return null;
		}

		return _mavenArtifactResolver.resolveOptional(coordinate, repositories, "sources", refresh);
	}

	/**
	 * Infers the likely repository order for a binary artifact based on its group and source cache.
	 *
	 * @param coordinate          the artifact coordinate
	 * @param fromMojangLibraries whether the binary was resolved from Mojang's library cache
	 *
	 * @return the candidate repository list in preferred order
	 */
	private static List<URI> inferredRepositories(MavenCoordinate coordinate, boolean fromMojangLibraries)
	{
		if (coordinate == null)
		{
			return List.of();
		}

		if (fromMojangLibraries)
		{
			return List.of(MOJANG_LIBRARIES, ToolchainMavenRepositories.MAVEN_CENTRAL, ToolchainMavenRepositories.FABRIC);
		}

		if (coordinate.groupId().startsWith("net.fabricmc"))
		{
			return List.of(ToolchainMavenRepositories.FABRIC, ToolchainMavenRepositories.MAVEN_CENTRAL);
		}

		return List.of(ToolchainMavenRepositories.MAVEN_CENTRAL, ToolchainMavenRepositories.FABRIC);
	}
}
