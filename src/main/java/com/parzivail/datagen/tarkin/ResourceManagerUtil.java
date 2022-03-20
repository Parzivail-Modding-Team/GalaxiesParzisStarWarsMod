package com.parzivail.datagen.tarkin;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

import java.util.ArrayList;
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

	public static <T> void forceReload(SinglePreparationResourceReloader<T> reloader, ResourceType type)
	{
		try (var resourceManager = new ReloadableResourceManagerImpl(type))
		{
			resourceManager.registerReloader(reloader);

			var list = new ArrayList<ResourcePack>();
			ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(resourcePackProfile -> list.add(resourcePackProfile.createResourcePack()));

			resourceManager.reload(Util.getMainWorkerExecutor(), Util.getMainWorkerExecutor(), CompletableFuture.completedFuture(Unit.INSTANCE), list);
		}
	}
}
