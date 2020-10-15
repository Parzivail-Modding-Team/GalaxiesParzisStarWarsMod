package com.parzivail.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ItemUtil
{
	public static boolean isInventoryEmpty(DefaultedList<ItemStack> inventory)
	{
		for (ItemStack itemStack : inventory)
			if (!ItemStack.EMPTY.equals(itemStack))
				return false;

		return true;
	}
}
