package dev.pswg.block.collection;

import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;

public class Dyed<T> extends HashMap<DyeColor, T>
{
	public Dyed(Function<DyeColor, T> generator)
	{
		for (var color : DyeColor.values())
			put(color, generator.apply(color));
	}
}