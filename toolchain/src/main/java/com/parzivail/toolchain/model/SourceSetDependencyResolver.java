package com.parzivail.toolchain.model;

/**
 * Resolves which source set of one module should be visible to another.
 */
public final class SourceSetDependencyResolver
{
	/**
	 * Prevents construction.
	 */
	private SourceSetDependencyResolver()
	{
	}

	/**
	 * Resolves which source set of a dependency should be visible to a consuming source set.
	 *
	 * <p>Client source sets must see client-only API from their dependencies, not just common code.
	 *
	 * @param graph                 the authoritative build graph
	 * @param dependencyId          the dependency module identifier
	 * @param consumerSourceSetName the consuming source-set name
	 *
	 * @return the source-set name to depend on
	 */
	public static String dependencySourceSetName(
			BuildGraph graph,
			String dependencyId,
			String consumerSourceSetName
	)
	{
		if (!SourceSetNames.CLIENT.equals(consumerSourceSetName))
		{
			return SourceSetNames.MAIN;
		}

		var dependency = graph.modules()
		                      .stream()
		                      .filter(candidate -> dependencyId.equals(candidate.id()))
		                      .findFirst()
		                      .orElseThrow(() -> new IllegalArgumentException("Unknown module id: " + dependencyId));

		return SourceSetLayout.hasClientSourceSet(dependency) ? SourceSetNames.CLIENT : SourceSetNames.MAIN;
	}
}
