package com.parzivail.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BlockUtils
{
	public static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos)
	{
		return false;
	}
}
