package dev.pswg.datagen;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

/**
 * A collection of utilities for working with resource managers during data generation
 */
public final class DataGenResourceHelper
{
	/**
	 * Forces a reload of the specified reloader and resource type
	 *
	 * @param reloaders The reloaders to load
	 * @param type      The type of resources to load
	 */
	public static void loadResources(PackType type, PreparableReloadListener... reloaders)
	{
		var list = new ArrayList<PackResources>();
		new ModResourcePackCreator(type).loadPacks(resourcePackProfile -> list.add(resourcePackProfile.open()));

		if (allResourceManagerReloadListeners(reloaders))
		{
			try (var resourceManager = new MultiPackResourceManager(type, list))
			{
				for (var reloader : reloaders)
				{
					((ResourceManagerReloadListener) reloader).onResourceManagerReload(resourceManager);
				}
			}
			return;
		}

		try (var resourceManager = new ReloadableResourceManager(type))
		{
			for (var reloader : reloaders)
				resourceManager.registerReloadListener(reloader);

			resourceManager.createReload(Util.backgroundExecutor(), Util.backgroundExecutor(), CompletableFuture.completedFuture(Unit.INSTANCE), list)
			               .done()
			               .join();
		}
	}

	/**
	 * Checks whether every requested datagen loader is a simple synchronous
	 * resource-manager listener.
	 *
	 * <p>When this is true we can invoke the listeners directly against a pack
	 * backed resource manager and avoid Fabric's full reload system.
	 *
	 * @param reloaders the listeners requested by datagen
	 * @return whether they can all be invoked directly
	 */
	private static boolean allResourceManagerReloadListeners(PreparableReloadListener... reloaders)
	{
		for (var reloader : reloaders)
		{
			if (!(reloader instanceof ResourceManagerReloadListener))
				return false;
		}

		return true;
	}
}
