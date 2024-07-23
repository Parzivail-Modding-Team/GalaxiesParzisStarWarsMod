package com.parzivail.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ItemUtil
{
	public static boolean isInventoryEmpty(DefaultedList<ItemStack> inventory)
	{
		return inventory.stream().allMatch(ItemStack::isEmpty);
	}
}
