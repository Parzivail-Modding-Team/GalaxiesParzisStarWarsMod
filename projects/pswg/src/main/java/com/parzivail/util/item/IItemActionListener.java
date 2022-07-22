package com.parzivail.util.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemActionListener
{
	void onItemAction(World world, PlayerEntity player, ItemStack stack, ItemAction action);
}
