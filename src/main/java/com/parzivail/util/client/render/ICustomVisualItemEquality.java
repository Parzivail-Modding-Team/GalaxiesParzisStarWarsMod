package com.parzivail.util.client.render;

import net.minecraft.item.ItemStack;

public interface ICustomVisualItemEquality
{
	boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated);
}
