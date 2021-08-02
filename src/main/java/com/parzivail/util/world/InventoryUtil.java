package com.parzivail.util.world;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryUtil
{
	public static int getSlotWithStack(Inventory inventory, ItemStack stack)
	{
		for (int i = 0; i < inventory.size(); i++)
			// purposefully checking for reference equality here
			if (inventory.getStack(i) == stack)
				return i;

		return -1;
	}
}
