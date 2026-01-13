package dev.pswg.item;

import dev.pswg.block.Dyed;
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