package com.parzivail.util.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface IItemEntityTickListener
{
	boolean onItemEntityTick(ItemEntity entity, ItemStack stack);
}
