package com.parzivail.util.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface INbtInitializer
{
	NBTTagCompound createNbtData(ItemStack stack);
}
