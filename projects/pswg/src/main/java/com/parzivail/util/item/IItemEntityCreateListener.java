package com.parzivail.util.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface IItemEntityCreateListener
{
	void onItemEntityCreated(ItemEntity entity, ItemStack stack);
}
