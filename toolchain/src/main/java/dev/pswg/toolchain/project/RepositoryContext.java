package dev.pswg.toolchain.project;

import dev.pswg.toolchain.config.ToolchainProjectConfig;
import dev.pswg.toolchain.config.ToolchainProjectConfigLoader;
import dev.pswg.toolchain.model.BuildGraph;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Discovers and caches the tracked host-project metadata that the standalone toolchain consumes.
 *
 * <p>The reusable toolchain lives under `toolchain/`, while the host project lives in the parent
 * directory and owns `toolchain.toml`, `gradle.properties`, `.idea`, and source roots.
 */
public final class RepositoryContext
{
	/**
	 * The shared Gradle properties file name.
	 */
	private static final String GRADLE_PROPERTIES_FILE = "gradle.properties";

	/**
	 * The standalone toolchain directory name.
	 */
	private static final String TOOLCHAIN_DIRECTORY = "toolchain";

	/**
	 * The standalone toolchain project root.
	 */
	private final Path _toolchainRoot;

	/**
	 * The tracked host-project repository root.
	 */
	private final Path _projectRoot;

	/**
	 * The IntelliJ project name.
	 */
	private final String _projectName;

	/**
	 * The tracked repository Gradle properties.
	 */
	private final Properties _gradleProperties;

	/**
	 * The authoritative configured build graph.
	 */
	private final BuildGraph _buildGraph;

	/**
	 * Creates a repository context from resolved paths and metadata.
	 */
	private RepositoryContext(
		Path toolchainRoot,
		Path projectRoot,
		String projectName,
		Properties gradleProperties,
		BuildGraph buildGraph
	)
	{
		_toolchainRoot = toolchainRoot;
		_projectRoot = projectRoot;
		_projectName = projectName;
		_gradleProperties = gradleProperties;
		_buildGraph = buildGraph;
	}

	/**
	 * Discovers the tracked repository context from either the standalone toolchain directory or the
	 * tracked host-project root.
	 *
	 * @return the discovered repository context
	 * @throws IOException if tracked metadata cannot be read
	 */
	public static RepositoryContext discoverFromWorkingDirectory() throws IOException
	{
		Path workingDirectory = Path.of("").toAbsolutePath().normalize();
		Path projectRoot = discoverProjectRoot(workingDirectory);
		Path toolchainRoot = projectRoot.resolve(TOOLCHAIN_DIRECTORY);
		ToolchainProjectConfig projectConfig = new ToolchainProjectConfigLoader().load(projectRoot);
		Properties gradleProperties = loadGradleProperties(projectRoot);

		return new RepositoryContext(
			toolchainRoot,
			projectRoot,
			projectConfig.projectName(),
			gradleProperties,
			projectConfig.toBuildGraph()
		);
	}

	/**
	 * Discovers the tracked host-project repository root from the current working directory.
	 *
	 * <p>The standalone toolchain runs both from its own project root and from the tracked host
	 * project. Walking upward keeps repository discovery stable in both modes.
	 *
	 * @param workingDirectory the current working directory
	 * @return the discovered repository root
	 * @throws IOException if no compatible repository root can be found
	 */
	private static Path discoverProjectRoot(Path workingDirectory) throws IOException
	{
		for (Path candidate = workingDirectory; candidate != null; candidate = candidate.getParent())
		{
			if (!Files.isRegularFile(candidate.resolve(GRADLE_PROPERTIES_FILE)))
			{
				continue;
			}

			if (Files.isDirectory(candidate.resolve(TOOLCHAIN_DIRECTORY)))
			{
				return candidate;
			}
		}

		throw new IOException("Could not discover the tracked project root from " + workingDirectory);
	}

	/**
	 * Gets the standalone toolchain project root.
	 *
	 * @return the toolchain project root
	 */
	public Path toolchainRoot()
	{
		return _toolchainRoot;
	}

	/**
	 * Gets the tracked host-project repository root.
	 *
	 * @return the tracked project root
	 */
	public Path projectRoot()
	{
		return _projectRoot;
	}

	/**
	 * Gets the IntelliJ project name.
	 *
	 * @return the IntelliJ project name
	 */
	public String projectName()
	{
		return _projectName;
	}

	/**
	 * Gets the tracked repository Gradle properties.
	 *
	 * @return the Gradle properties
	 */
	public Properties gradleProperties()
	{
		return _gradleProperties;
	}

	/**
	 * Gets the authoritative configured build graph.
	 *
	 * @return the build graph
	 */
	public BuildGraph buildGraph()
	{
		return _buildGraph;
	}

	/**
	 * Gets the tracked Minecraft version from the authoritative graph.
	 *
	 * @return the tracked Minecraft version
	 */
	public String minecraftVersion()
	{
		return _buildGraph.minecraftVersion();
	}

	/**
	 * Loads the tracked repository Gradle properties.
	 *
	 * @param projectRoot the tracked project root
	 * @return the parsed Gradle properties
	 * @throws IOException if the properties file cannot be read
	 */
	private static Properties loadGradleProperties(Path projectRoot) throws IOException
	{
		Properties properties = new Properties();
		Path path = projectRoot.resolve(GRADLE_PROPERTIES_FILE);

		try (InputStream inputStream = Files.newInputStream(path))
		{
			properties.load(inputStream);
		}

		return properties;
	}
}
