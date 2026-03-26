package com.parzivail.toolchain.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared source-set layout helpers used by generated IntelliJ metadata and bespoke compilation.
 */
public final class SourceSetLayout
{
	/**
	 * Prevents construction.
	 */
	private SourceSetLayout()
	{
	}

	/**
	 * Gets the Java source roots for one module source set.
	 *
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the Java source roots
	 */
	public static List<Path> sourceRoots(ModuleSpec module, String sourceSetName)
	{
		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			return module.clientSources();
		}

		return module.mainSources();
	}

	/**
	 * Gets the resource roots for one module source set.
	 *
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the resource roots
	 */
	public static List<Path> resourceRoots(ModuleSpec module, String sourceSetName)
	{
		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			return module.clientResources();
		}

		List<Path> roots = new ArrayList<>(module.mainResources());

		if (module.datagenOutput() != null && !roots.contains(module.datagenOutput()))
		{
			roots.add(module.datagenOutput());
		}

		return roots;
	}

	/**
	 * Gets the generated Java roots for one module source set.
	 *
	 * @param module        the module specification
	 * @param sourceSetName the source-set name
	 *
	 * @return the generated source roots
	 */
	public static List<Path> generatedRoots(ModuleSpec module, String sourceSetName)
	{
		if (SourceSetNames.CLIENT.equals(sourceSetName))
		{
			return module.generatedClientSources();
		}

		return module.generatedSources();
	}

	/**
	 * Checks whether one module materially has a client source set.
	 *
	 * @param module the module specification
	 *
	 * @return whether the module has client sources, resources, or generated client roots
	 */
	public static boolean hasClientSourceSet(ModuleSpec module)
	{
		return !module.clientSources().isEmpty()
		       || !module.clientResources().isEmpty()
		       || !module.generatedClientSources().isEmpty();
	}
}
