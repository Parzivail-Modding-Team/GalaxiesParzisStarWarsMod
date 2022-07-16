package com.parzivail.util.registry;

import net.minecraft.util.DyeColor;

import java.util.HashMap;
import java.util.function.Function;

public class DyedStoneProducts extends HashMap<DyeColor, StoneProducts>
{
	public DyedStoneProducts(Function<DyeColor, StoneProducts> blockFunction)
	{
		for (var color : DyeColor.values())
			put(color, blockFunction.apply(color));
	}
}
