package dev.pswg.block.collection;

import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class DyedBlocks extends Dyed<Block>
{
	public DyedBlocks(Function<DyeColor, Block> blockFunction)
	{
		super(blockFunction);
	}
}