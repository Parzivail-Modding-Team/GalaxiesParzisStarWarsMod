package dev.pswg.block.collection;

import dev.pswg.block.Dyed;
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