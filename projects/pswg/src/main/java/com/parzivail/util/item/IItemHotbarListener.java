package com.parzivail.util.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IItemHotbarListener
{
	default boolean onItemSelected(PlayerEntity player, ItemStack stack)
	{
		return false;
	}

	default boolean onItemDeselected(PlayerEntity player, ItemStack stack)
	{
		return false;
	}
}
