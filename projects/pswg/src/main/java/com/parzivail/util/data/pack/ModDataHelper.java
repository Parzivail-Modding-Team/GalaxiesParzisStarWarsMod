package com.parzivail.util.data.pack;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import java.util.function.Consumer;

public class ModDataHelper
{
	public static void withResources(ResourceType type, Consumer<ResourceManager> resourceConsumer)
	{
		var packs = FabricLoader
				.getInstance()
				.getAllMods()
				.stream()
				.filter(modContainer -> !modContainer.getMetadata().getType().equals("builtin"))
				.map(mod -> (ResourcePack)(new ModNioResourcePack(mod)))
				.toList();

		var resourceManager = new LifecycledResourceManagerImpl(type, packs);
		resourceConsumer.accept(resourceManager);

		for (var pack : packs)
			pack.close();
	}
}

