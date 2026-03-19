package dev.pswg.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class DoorInsertItem extends Item
{
	private DyeColor color;

	public DoorInsertItem(DyeColor color, Properties settings)
	{
		super(settings);
		this.color = color;
	}

	public DyeColor getColor()
	{
		return color;
	}
}