package com.parzivail.util.registry;

import net.minecraft.util.DyeColor;

import java.util.HashMap;
import java.util.function.Function;

public class Dyed<T> extends HashMap<DyeColor, T>
{
	public Dyed(Function<DyeColor, T> generator)
	{
		for (var color : DyeColor.values())
			put(color, generator.apply(color));
	}
}
