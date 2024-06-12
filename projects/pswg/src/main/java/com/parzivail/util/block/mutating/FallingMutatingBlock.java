package com.parzivail.util.block.mutating;

import com.mojang.serialization.MapCodec;
import com.parzivail.util.block.PFallingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public abstract class FallingMutatingBlock extends PFallingBlock
{
	private final Block target;
	private final int meanTransitionTime;

	public FallingMutatingBlock(Block target, int meanTransitionTime, ColorCode dustColor, Settings settings)
	{
		super(dustColor, settings.ticksRandomly());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	@Override
	protected abstract MapCodec<? extends FallingMutatingBlock> getCodec();

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
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
