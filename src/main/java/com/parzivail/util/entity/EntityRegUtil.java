package com.parzivail.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.GameData;

public class EntityRegUtil
{
	public static void registerEntityWithEgg(Object mod, String modId, Class<? extends Entity> clazz, String name, int entityId, int bgColor, int fgColor)
	{
		registerEntity(mod, modId, clazz, name, 80, entityId, 3, true);
		int nextEggID = getEggId();
		if (nextEggID < 65536)
		{
			ResourceLocation resourcelocation = new ResourceLocation(modId, name);
			EntityList.ENTITY_EGGS.put(resourcelocation, new EntityList.EntityEggInfo(resourcelocation, bgColor, fgColor));
		}
	}

	public static void registerEntity(Object mod, String modId, Class<? extends Entity> clazz, String name, int entityId, int trackingDistance, int updateFrequency, boolean sendsVelocityUpdates)
	{
		ResourceLocation registryName = new ResourceLocation(modId, name);
		EntityRegistry.registerModEntity(registryName, clazz, modId + "." + name, entityId, mod, trackingDistance, updateFrequency, sendsVelocityUpdates);
	}

	public static int getEggId()
	{
		int eggID = 0;
		do
			eggID++;
		while (GameData.getEntityRegistry().getValue(eggID) != null);

		return eggID;
	}
}
