package com.parzivail.util.item;

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
		if (stack == null || stack.stackTagCompound != null)
			return;
		stack.stackTagCompound = new NBTTagCompound();
	}
}
