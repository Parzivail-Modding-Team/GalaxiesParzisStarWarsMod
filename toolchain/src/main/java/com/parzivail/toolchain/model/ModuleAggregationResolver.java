package com.parzivail.toolchain.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolves the modeled module closure used by aggregated development-time
 * workflows.
 */
public final class ModuleAggregationResolver
{
	private ModuleAggregationResolver()
	{
	}

	/**
	 * Resolves a module by identifier.
	 *
	 * @param graph    the authoritative build graph
	 * @param moduleId the module identifier
	 *
	 * @return the resolved module
	 */
	public static ModuleSpec requireModule(BuildGraph graph, String moduleId)
	{
		return graph.modules()
		            .stream()
		            .filter(candidate -> moduleId.equals(candidate.id()))
		            .findFirst()
		            .orElseThrow(() -> new IllegalArgumentException("Unknown module id: " + moduleId));
	}

	/**
	 * Resolves the full ordered aggregation closure for a root module.
	 *
	 * @param graph        the authoritative build graph
	 * @param rootModuleId the root module identifier
	 *
	 * @return the root module followed by its aggregated module closure
	 */
	public static List<ModuleSpec> aggregatedModules(BuildGraph graph, String rootModuleId)
	{
		List<ModuleSpec> modules = new ArrayList<>();
		Set<String> visited = new LinkedHashSet<>();
		collect(graph, rootModuleId, visited, modules);
		return modules;
	}

	/**
	 * Resolves the ordered aggregated module closure excluding the root module
	 * itself.
	 *
	 * @param graph        the authoritative build graph
	 * @param rootModuleId the root module identifier
	 *
	 * @return the aggregated dependency modules
	 */
	public static List<ModuleSpec> aggregatedDependencies(BuildGraph graph, String rootModuleId)
	{
		var modules = aggregatedModules(graph, rootModuleId);

		if (!modules.isEmpty())
		{
			modules.removeFirst();
		}

		return modules;
	}

	/**
	 * Collects the aggregation closure for one module.
	 *
	 * @param graph    the authoritative build graph
	 * @param moduleId the module identifier to collect
	 * @param visited  the visited module ids
	 * @param modules  the accumulated ordered modules
	 */
	private static void collect(
			BuildGraph graph,
			String moduleId,
			Set<String> visited,
			List<ModuleSpec> modules
	)
	{
		if (!visited.add(moduleId))
		{
			return;
		}

		var module = requireModule(graph, moduleId);
		modules.add(module);

		for (var dependencyId : module.dependencies())
		{
			collect(graph, dependencyId, visited, modules);
		}

		for (var memberId : module.aggregateMembers())
		{
			collect(graph, memberId, visited, modules);
		}
	}
}
