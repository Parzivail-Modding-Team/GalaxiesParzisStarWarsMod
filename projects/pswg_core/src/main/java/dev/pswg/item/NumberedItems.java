package dev.pswg.item;

import dev.pswg.util.Numbered;
import java.util.function.Function;
import net.minecraft.world.item.Item;

public class NumberedItems extends Numbered<Item>
{
	public NumberedItems(int count, Function<Integer, Item> itemFunction)
	{
		super(count, itemFunction);
	}
}