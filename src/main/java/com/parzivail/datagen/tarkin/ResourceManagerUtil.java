package com.parzivail.datagen.tarkin;

import com.parzivail.util.data.IExternalReloadResourceManager;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.profiler.DummyProfiler;

import java.util.concurrent.CompletableFuture;

public class ResourceManagerUtil
{
	private static class DummySynchronizer implements ResourceReloader.Synchronizer
	{
		public static final DummySynchronizer INSTANCE = new DummySynchronizer();

		private DummySynchronizer()
		{
		}

		@Override
		public <T> CompletableFuture<T> whenPrepared(T preparedObject)
		{
			return CompletableFuture.completedFuture(preparedObject);
		}
	}

	public static <T> void forceReload(IExternalReloadResourceManager<T> reloader, ResourceType type)
	{
		var resourceManager = new ReloadableResourceManagerImpl(type);
		ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(resourcePackProfile -> {
			resourceManager.addPack(resourcePackProfile.createResourcePack());
		});
		reloader.apply(reloader.prepare(resourceManager, DummyProfiler.INSTANCE), resourceManager, DummyProfiler.INSTANCE);
	}
}
