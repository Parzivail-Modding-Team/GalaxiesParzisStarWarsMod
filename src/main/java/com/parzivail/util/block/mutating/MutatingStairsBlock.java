package com.parzivail.util.block.mutating;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class MutatingStairsBlock extends StairsBlock
{
	private final StairsBlock target;
	private final int meanTransitionTime;

	public MutatingStairsBlock(BlockState baseBlockState, StairsBlock target, int meanTransitionTime, AbstractBlock.Settings settings)
	{
		super(baseBlockState, settings.ticksRandomly());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (!canTransition(state, world, pos, random))
			return;

		if (random.nextInt(meanTransitionTime) == 0)
			world.setBlockState(pos, target.getStateWithProperties(state), NOTIFY_LISTENERS);
	}

	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return true;
	}
}
