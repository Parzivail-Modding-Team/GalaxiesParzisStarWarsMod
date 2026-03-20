package dev.pswg.toolchain.fabric;

/**
 * A Maven coordinate in {@code group:artifact:version} form.
 *
 * @param groupId the group identifier
 * @param artifactId the artifact identifier
 * @param version the artifact version
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
	 * @return the parsed coordinate
	 */
	public static MavenCoordinate parse(String notation)
	{
		String[] parts = notation.split(":");

		if (parts.length != 3)
		{
			throw new IllegalArgumentException("Unsupported Maven coordinate: " + notation);
		}

		return new MavenCoordinate(parts[0], parts[1], parts[2]);
	}

	/**
	 * Returns the artifact path relative to a Maven repository root.
	 *
	 * @return the repository-relative artifact path
	 */
	public String repositoryPath()
	{
		return groupId.replace('.', '/')
			+ "/"
			+ artifactId
			+ "/"
			+ version
			+ "/"
			+ artifactId
			+ "-"
			+ version
			+ ".jar";
	}
}
