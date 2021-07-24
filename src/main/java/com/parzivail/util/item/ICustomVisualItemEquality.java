package com.parzivail.util.item;

import net.minecraft.item.ItemStack;

public interface ICustomVisualItemEquality
{
	boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated);
}
