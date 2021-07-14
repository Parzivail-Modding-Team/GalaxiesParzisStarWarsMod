package com.parzivail.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ItemUtil
{
	public static boolean isInventoryEmpty(DefaultedList<ItemStack> inventory)
	{
		for (var itemStack : inventory)
			if (!ItemStack.EMPTY.equals(itemStack))
				return false;

		return true;
	}

	public static boolean areStacksEqualIgnoreCount(ItemStack left, ItemStack right)
	{
		if (left.getItem() != right.getItem())
			return false;
		else if (left.getTag() == null && right.getTag() != null)
			return false;
		else
			return left.getTag() == null || left.getTag().equals(right.getTag());
	}
}
