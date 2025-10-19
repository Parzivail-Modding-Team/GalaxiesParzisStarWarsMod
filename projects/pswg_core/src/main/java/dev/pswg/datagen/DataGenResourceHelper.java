package dev.pswg.datagen;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.v1.SetupMarkerResourceReloader;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
	public static void loadResources(ResourceType type, SynchronousResourceReloader... reloaders)
	{
		try (var resourceManager = new ReloadableResourceManagerImpl(type))
		{
			resourceManager.registerReloader(new SetupMarkerResourceReloader(new DynamicRegistryManager.ImmutableImpl(List.of()), FeatureSet.empty()));

			for (var reloader : reloaders)
				resourceManager.registerReloader(reloader);

			var list = new ArrayList<ResourcePack>();
			new ModResourcePackCreator(type).register(resourcePackProfile -> list.add(resourcePackProfile.createResourcePack()));

			resourceManager.reload(Util.getMainWorkerExecutor(), Util.getMainWorkerExecutor(), CompletableFuture.completedFuture(Unit.INSTANCE), list)
			               .whenComplete()
			               .join();
		}
	}
}
