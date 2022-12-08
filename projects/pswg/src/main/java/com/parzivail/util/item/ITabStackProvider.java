package com.parzivail.util.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ITabStackProvider
{
	void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks);
}
