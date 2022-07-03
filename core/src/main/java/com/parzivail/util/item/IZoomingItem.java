package com.parzivail.util.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IZoomingItem
{
	double getFovMultiplier(ItemStack stack, World world, PlayerEntity entity);
}
