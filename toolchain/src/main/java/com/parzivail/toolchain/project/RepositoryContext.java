package com.parzivail.toolchain.project;

import com.parzivail.toolchain.config.ToolchainProjectConfig;
import com.parzivail.toolchain.config.ToolchainProjectConfigLoader;
import com.parzivail.toolchain.model.BuildGraph;
import com.parzivail.toolchain.path.ToolchainPaths;

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
		String projectName,
		Properties gradleProperties,
		BuildGraph buildGraph
	)
	{
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
		ToolchainProjectConfig projectConfig = new ToolchainProjectConfigLoader().load();
		Properties gradleProperties = loadGradleProperties();

		return new RepositoryContext(
			projectConfig.projectName(),
			gradleProperties,
			projectConfig.toBuildGraph()
		);
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
	 * @return the parsed Gradle properties
	 * @throws IOException if the properties file cannot be read
	 */
	public static Properties loadGradleProperties() throws IOException
	{
		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(ToolchainPaths.GRADLE_PROPERTIES_FILE))
		{
			properties.load(inputStream);
		}

		return properties;
	}
}
