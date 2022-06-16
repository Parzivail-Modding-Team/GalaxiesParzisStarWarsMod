package com.parzivail.util.block.mutating;

import com.parzivail.util.block.VerticalSlabBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class MutatingSlabBlock extends VerticalSlabBlock
{
	private final VerticalSlabBlock target;
	private final int meanTransitionTime;

	public MutatingSlabBlock(VerticalSlabBlock target, int meanTransitionTime, Settings settings)
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
