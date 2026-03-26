package com.parzivail.toolchain.fabric;

import java.nio.file.Path;

/**
 * A Maven coordinate in {@code group:artifact:version} form.
 *
 * @param groupId    the group identifier
 * @param artifactId the artifact identifier
 * @param version    the artifact version
 */
public record MavenCoordinate(
		String groupId,
		String artifactId,
		String version
)
{
	/**
	 * Parses a Maven coordinate string.
	 *
	 * @param notation the coordinate notation
	 *
	 * @return the parsed coordinate
	 */
	public static MavenCoordinate parse(String notation)
	{
		var parts = notation.split(":");

		if (parts.length != 3)
		{
			throw new IllegalArgumentException("Unsupported Maven coordinate: " + notation);
		}

		return new MavenCoordinate(parts[0], parts[1], parts[2]);
	}

	/**
	 * Parses a Maven-style repository-relative jar path.
	 *
	 * @param repositoryPath the repository-relative jar path
	 *
	 * @return the parsed coordinate, or {@code null} when the path is not a standard Maven artifact
	 */
	public static MavenCoordinate parseRepositoryPath(Path repositoryPath)
	{
		if (repositoryPath == null || repositoryPath.getNameCount() < 4)
		{
			return null;
		}

		var fileName = repositoryPath.getFileName().toString();
		var version = repositoryPath.getName(repositoryPath.getNameCount() - 2).toString();
		var artifactId = repositoryPath.getName(repositoryPath.getNameCount() - 3).toString();

		if (!fileName.startsWith(artifactId + "-" + version) || !fileName.endsWith(".jar"))
		{
			return null;
		}

		var groupId = new StringBuilder();

		for (var i = 0; i < repositoryPath.getNameCount() - 3; i++)
		{
			if (!groupId.isEmpty())
			{
				groupId.append('.');
			}

			groupId.append(repositoryPath.getName(i));
		}

		if (groupId.isEmpty())
		{
			return null;
		}

		return new MavenCoordinate(groupId.toString(), artifactId, version);
	}

	/**
	 * Returns the artifact path relative to a Maven repository root for one classifier variant.
	 *
	 * @param classifier the optional classifier
	 *
	 * @return the repository-relative artifact path
	 */
	public String repositoryPath(String classifier)
	{
		return groupId.replace('.', '/')
		       + "/"
		       + artifactId
		       + "/"
		       + version
		       + "/"
		       + artifactFileName(classifier);
	}

	/**
	 * Returns the standard jar filename for this coordinate and an optional classifier.
	 *
	 * @param classifier the optional classifier
	 *
	 * @return the artifact filename
	 */
	public String artifactFileName(String classifier)
	{
		return artifactId
		       + "-"
		       + version
		       + (classifier == null || classifier.isBlank() ? "" : "-" + classifier)
		       + ".jar";
	}
}
