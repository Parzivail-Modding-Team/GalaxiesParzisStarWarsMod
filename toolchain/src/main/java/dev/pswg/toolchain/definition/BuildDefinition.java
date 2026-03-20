package dev.pswg.toolchain.definition;

import dev.pswg.toolchain.model.BuildGraph;

/**
 * Defines a complete PSWG build graph.
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
