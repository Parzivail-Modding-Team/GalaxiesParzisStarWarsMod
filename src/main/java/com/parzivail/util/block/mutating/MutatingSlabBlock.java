package com.parzivail.util.block.mutating;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class MutatingSlabBlock extends SlabBlock
{
	private final SlabBlock target;
	private final int meanTransitionTime;

	public MutatingSlabBlock(SlabBlock target, int meanTransitionTime, Settings settings)
	{
		super(settings.ticksRandomly());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (!canTransition(state, world, pos, random))
			return;

		if (random.nextInt(meanTransitionTime) == 0)
			world.setBlockState(pos, target.getDefaultState().with(TYPE, state.get(TYPE)).with(WATERLOGGED, state.get(WATERLOGGED)), 2);
	}

	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return true;
	}
}
