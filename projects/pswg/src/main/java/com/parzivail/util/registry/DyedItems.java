package com.parzivail.util.registry;

import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;

import java.util.function.Function;

public class DyedItems extends Dyed<Item>
{
	public DyedItems(Function<DyeColor, Item> blockFunction)
	{
		super(blockFunction);
	}
}
