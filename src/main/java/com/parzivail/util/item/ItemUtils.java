package com.parzivail.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by colby on 12/28/2017.
 */
public class ItemUtils
{
	public static void ensureNbt(ItemStack stack)
	{
		if (stack == null || stack.hasTagCompound())
			return;
		stack.setTagCompound(new NBTTagCompound());
	}

	public static NBTTagCompound ensureNbt(NBTTagCompound tag)
	{
		if (tag != null)
			return tag;
		return new NBTTagCompound();
	}
}
