package com.parzivail.util.data;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;

public interface IExternalReloadResourceManager<T>
{
	/**
	 * Prepares the intermediate object.
	 *
	 * <p>This method is called in the prepare executor in a reload.
	 *
	 * @param profiler the prepare profiler
	 * @param manager  the resource manager
	 *
	 * @return the prepared object
	 */
	T prepare(ResourceManager manager, Profiler profiler);

	/**
	 * Handles the prepared intermediate object.
	 *
	 * <p>This method is called in the apply executor, or the game engine, in a
	 * reload.
	 *
	 * @param manager  the resource manager
	 * @param profiler the apply profiler
	 * @param prepared the prepared object
	 */
	void apply(T prepared, ResourceManager manager, Profiler profiler);
}
