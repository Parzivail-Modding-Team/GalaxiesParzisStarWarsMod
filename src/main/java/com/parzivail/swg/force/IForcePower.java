package com.parzivail.swg.force;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IForcePower
{
	boolean canUse(World world, EntityPlayer player);

	boolean use(World world, EntityPlayer player);

	void tick(World world, EntityPlayer player);

	String getId();
}
