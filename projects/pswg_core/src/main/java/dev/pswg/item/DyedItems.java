package dev.pswg.item;

import dev.pswg.block.collection.Dyed;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class DyedItems extends Dyed<Item>
{
	public DyedItems(Function<DyeColor, Item> blockFunction)
	{
		super(blockFunction);
	}
}