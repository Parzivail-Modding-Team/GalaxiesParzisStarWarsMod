package com.parzivail.toolchain.definition;

import com.parzivail.toolchain.model.BuildGraph;

/**
 * Defines a complete toolchain build graph.
 */
public interface BuildDefinition
{
	/**
	 * Materializes the build graph.
	 *
	 * @return the materialized graph
	 */
	BuildGraph define();
}
