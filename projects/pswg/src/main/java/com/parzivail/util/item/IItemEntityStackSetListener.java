package com.parzivail.util.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface IItemEntityStackSetListener
{
	void onItemEntityStackSet(ItemEntity entity, ItemStack stack);
}
