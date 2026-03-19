package dev.pswg.block.collection;

import dev.pswg.util.Numbered;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;

public class NumberedBlocks extends Numbered<Block>
{
	public NumberedBlocks(int count, Function<Integer, Block> blockFunction)
	{
		super(count, blockFunction);
	}
}