package com.parzivail.util.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by colby on 12/28/2017.
 */
public class ItemUtils
{
	public static final ItemStack ITEMSTACK_EMPTY = new ItemStack(Blocks.air);
	public static final ItemStack[] ITEMSTACK_EMPTY_ARRAY = new ItemStack[0];

	public static void ensureNbt(ItemStack stack)
	{
		if (stack == null)
			return;
		stack.stackTagCompound = ensureNbt(stack.stackTagCompound);
	}

	public static NBTTagCompound ensureNbt(NBTTagCompound tag)
	{
		if (tag != null)
			return tag;
		return new NBTTagCompound();
	}

	public static boolean hasItems(EntityPlayer player, ItemStack stack)
	{
		InventoryPlayer inv = player.inventory;

		int amount = 0;
		for (int i = 0; i < inv.mainInventory.length; ++i)
			if (inv.mainInventory[i] != null && inv.mainInventory[i].isItemEqual(stack))
				amount += inv.mainInventory[i].stackSize;

		return amount >= stack.stackSize;
	}
}
