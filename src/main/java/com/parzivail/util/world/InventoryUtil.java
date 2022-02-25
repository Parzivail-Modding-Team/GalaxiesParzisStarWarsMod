package com.parzivail.util.world;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

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

	public static void writeToNbt(NbtCompound nbt, String root, DefaultedList<ItemStack> items)
	{
		var nbtList = new NbtList();
		for (var itemStack : items)
		{
			var nbtCompound = new NbtCompound();
			if (!itemStack.isEmpty())
				itemStack.writeNbt(nbtCompound);

			nbtList.add(nbtCompound);
		}
		nbt.put(root, nbtList);
	}

	public static void readFromNbt(NbtCompound nbt, String root, DefaultedList<ItemStack> items)
	{
		if (nbt.contains(root, NbtElement.LIST_TYPE))
		{
			var nbtList = nbt.getList(root, NbtElement.COMPOUND_TYPE);
			for (var i = 0; i < items.size(); ++i)
				items.set(i, ItemStack.fromNbt(nbtList.getCompound(i)));
		}
	}
}
