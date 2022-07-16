package com.parzivail.util.block;

import com.parzivail.util.registry.DyedBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.function.ToIntFunction;

public class BlockUtil
{
	public static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel)
	{
		return (blockState) -> (Boolean)blockState.get(Properties.LIT) ? litLevel : 0;
	}

	public static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType)
	{
		return false;
	}

	public static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos)
	{
		return false;
	}

	public static boolean always(BlockState blockState, BlockView blockView, BlockPos blockPos)
	{
		return true;
	}

	public static Block[] concat(DyedBlocks dyedSet, Block... blockSet)
	{
		var blocks = new Block[dyedSet.size() + blockSet.length];
		var i = 0;

		for (var block : dyedSet.values())
			blocks[i++] = block;

		for (var block : blockSet)
			blocks[i++] = block;

		return blocks;
	}
}
