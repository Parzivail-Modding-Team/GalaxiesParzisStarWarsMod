package com.parzivail.pswg.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ClientUtil
{
	public static void spawnEntity(World world, Entity entity)
	{
		if (!(world instanceof ClientWorld))
			return;

		ClientWorld clientWorld = (ClientWorld)world;
		clientWorld.addEntity(entity.getEntityId(), entity);
	}
}
