package com.parzivail.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ItemUtil
{
	public static boolean isInventoryEmpty(DefaultedList<ItemStack> inventory)
	{
		return inventory.stream().allMatch(ItemStack::isEmpty);
	}

	public static boolean areStacksEqualIgnoreCount(ItemStack left, ItemStack right)
	{
		if (left.getItem() != right.getItem())
			return false;
		else if (left.getNbt() == null && right.getNbt() != null)
			return false;
		else
			return left.getNbt() == null || left.getNbt().equals(right.getNbt());
	}
}
