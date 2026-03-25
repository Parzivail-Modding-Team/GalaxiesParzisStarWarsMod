package com.parzivail.toolchain.model;

import java.util.List;

/**
 * Passive representation of a PSWG build graph.
 *
 * @param projectId the logical project identifier
 * @param minecraftVersion the selected Minecraft version
 * @param developmentModuleId the default injected module for the supported development workflow
 * @param modules the declared modules in the graph
 */
public record BuildGraph(
	String projectId,
	String minecraftVersion,
	String developmentModuleId,
	List<ModuleSpec> modules
)
{
	/**
	 * Creates a graph with defensive copies.
	 *
	 * @param projectId the logical project identifier
	 * @param minecraftVersion the selected Minecraft version
	 * @param developmentModuleId the default injected development module
	 * @param modules the declared modules in the graph
	 */
	public BuildGraph
	{
		modules = List.copyOf(modules);
	}
}
