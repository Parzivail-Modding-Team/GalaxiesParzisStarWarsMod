package com.parzivail.toolchain.project;

import com.parzivail.toolchain.config.ToolchainProjectConfig;
import com.parzivail.toolchain.config.ToolchainProjectConfigLoader;
import com.parzivail.toolchain.model.BuildGraph;

import java.io.IOException;

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
	 * The Fabric Loader version.
	 */
	private final String _fabricLoaderVersion;

	/**
	 * The authoritative configured build graph.
	 */
	private final BuildGraph _buildGraph;

	/**
	 * Creates a repository context from resolved paths and metadata.
	 */
	private RepositoryContext(ToolchainProjectConfig config)
	{
		_projectName = config.projectName();
		_fabricLoaderVersion = config.fabricLoaderVersion();
		_buildGraph = config.toBuildGraph();
	}

	/**
	 * Discovers the tracked repository context from either the standalone toolchain directory or the
	 * tracked host-project root.
	 *
	 * @return the discovered repository context
	 *
	 * @throws IOException if tracked metadata cannot be read
	 */
	public static RepositoryContext load() throws IOException
	{
		return new RepositoryContext(new ToolchainProjectConfigLoader().load());
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
	 * Gets the tracked Fabric Loader version from the authoritative graph.
	 *
	 * @return the tracked Fabric Loader version
	 */
	public String loaderVersion()
	{
		return _fabricLoaderVersion;
	}
}
