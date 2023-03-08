package com.parzivail.util.registry;

import com.parzivail.util.generics.Dyed;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

import java.util.function.Function;

public class DyedBlocks extends Dyed<Block>
{
	public DyedBlocks(Function<DyeColor, Block> blockFunction)
	{
		super(blockFunction);
	}
}
