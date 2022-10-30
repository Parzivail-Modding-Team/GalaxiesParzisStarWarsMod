package com.parzivail.pswg.item;

import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;

public class DoorInsertItem extends Item
{
	private DyeColor color;

	public DoorInsertItem(DyeColor color, Settings settings)
	{
		super(settings);
		this.color = color;
	}

	public DyeColor getColor()
	{
		return color;
	}
}
