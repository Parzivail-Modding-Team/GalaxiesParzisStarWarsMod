package com.parzivail.util.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface IItemEntityConsumer
{
	void onItemEntityCreated(ItemEntity entity, ItemStack stack);
}
