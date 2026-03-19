package dev.pswg.datagen;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.v1.SetupMarkerResourceReloader;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
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
	public static void loadResources(PackType type, PreparableReloadListener... reloaders)
	{
		try (var resourceManager = new ReloadableResourceManager(type))
		{
			resourceManager.registerReloadListener(new SetupMarkerResourceReloader(new RegistryAccess.ImmutableRegistryAccess(List.of()), FeatureFlagSet.of()));

			for (var reloader : reloaders)
				resourceManager.registerReloadListener(reloader);

			var list = new ArrayList<PackResources>();
			new ModResourcePackCreator(type).loadPacks(resourcePackProfile -> list.add(resourcePackProfile.open()));

			resourceManager.createReload(Util.backgroundExecutor(), Util.backgroundExecutor(), CompletableFuture.completedFuture(Unit.INSTANCE), list)
			               .done()
			               .join();
		}
	}
}
