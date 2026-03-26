package com.parzivail.toolchain.config;

import com.parzivail.toolchain.model.BuildGraph;
import com.parzivail.toolchain.model.ModuleSpec;

import java.util.List;

/**
 * Text-backed project configuration loaded from the tracked host repository.
 *
 * @param projectId                  the logical project identifier
 * @param projectName                the IntelliJ-facing project name
 * @param minecraftVersion           the tracked Minecraft version
 * @param fabricLoaderVersion        the tracked Fabric loader version
 * @param defaultDevelopmentModuleId the default injected development module
 * @param modules                    the configured modules
 */
public record ToolchainProjectConfig(
		String projectId,
		String projectName,
		String minecraftVersion,
		String fabricLoaderVersion,
		String defaultDevelopmentModuleId,
		List<ModuleSpec> modules
)
{
	public ToolchainProjectConfig
	{
		modules = List.copyOf(modules);
	}

	/**
	 * Materializes the authoritative build graph from the loaded configuration.
	 *
	 * @return the materialized build graph
	 */
	public BuildGraph toBuildGraph()
	{
		return new BuildGraph(
				projectId,
				minecraftVersion,
				fabricLoaderVersion,
				defaultDevelopmentModuleId,
				modules
		);
	}
}
