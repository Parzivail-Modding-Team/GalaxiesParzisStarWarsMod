package dev.pswg.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class ItemUtil
{
	public static boolean isInventoryEmpty(NonNullList<ItemStack> inventory)
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
		else if (left.isEmpty() && !right.isEmpty())
			return false;
		else
			return left.isEmpty() || left.getComponents().equals(right.getComponents());
	}
}
