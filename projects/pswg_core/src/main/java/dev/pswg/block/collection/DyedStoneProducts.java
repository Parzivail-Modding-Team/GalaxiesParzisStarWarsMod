package dev.pswg.block.collection;

import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;

public class DyedStoneProducts extends HashMap<DyeColor, StoneProducts>
{
	public DyedStoneProducts(Function<DyeColor, StoneProducts> blockFunction)
	{
		for (var color : DyeColor.values())
			put(color, blockFunction.apply(color));
	}
}