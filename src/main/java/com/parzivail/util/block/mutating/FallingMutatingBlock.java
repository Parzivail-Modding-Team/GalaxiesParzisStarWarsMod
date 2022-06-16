package com.parzivail.util.block.mutating;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class FallingMutatingBlock extends FallingBlock
{
	private final Block target;
	private final int meanTransitionTime;

	public FallingMutatingBlock(Block target, int meanTransitionTime, Settings settings)
	{
		super(settings.ticksRandomly());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	@Override
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
