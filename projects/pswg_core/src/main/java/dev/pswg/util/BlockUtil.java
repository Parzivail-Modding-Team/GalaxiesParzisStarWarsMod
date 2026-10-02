package dev.pswg.util;

import dev.pswg.block.collection.DyedBlocks;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class BlockUtil
{
	public static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel)
	{
		return (blockState) -> (Boolean)blockState.getValue(BlockStateProperties.LIT) ? litLevel : 0;
	}

	public static boolean never(BlockState blockState, BlockGetter blockView, BlockPos blockPos, EntityType<?> entityType)
	{
		return false;
	}

	/**
	 * Predicate that disables view blocking for a block state.
	 *
	 * @param blockState the block state
	 * @param blockView the block view
	 * @param blockPos the block position
	 * @param viewBlockingBox the view-blocking query box
	 * @return {@code false}
	 */
	public static boolean never(BlockState blockState, BlockGetter blockView, BlockPos blockPos, AABB viewBlockingBox)
	{
		return false;
	}

	public static boolean never(BlockState blockState, BlockGetter blockView, BlockPos blockPos)
	{
		return false;
	}

	public static boolean always(BlockState blockState, BlockGetter blockView, BlockPos blockPos)
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
