package com.parzivail.util.registry;

import net.minecraft.item.Item;

import java.util.function.Function;

public class NumberedItems extends Numbered<Item>
{
	public NumberedItems(int count, Function<Integer, Item> itemFunction)
	{
		super(count, itemFunction);
	}
}
