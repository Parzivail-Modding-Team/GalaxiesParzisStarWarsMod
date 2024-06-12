package com.parzivail.util.block.mutating;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public abstract class MutatingStairsBlock extends StairsBlock
{
	private final StairsBlock target;
	private final int meanTransitionTime;

	public MutatingStairsBlock(BlockState baseBlockState, StairsBlock target, int meanTransitionTime, AbstractBlock.Settings settings)
	{
		super(baseBlockState, settings.ticksRandomly());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	@Override
	public abstract MapCodec<? extends MutatingStairsBlock> getCodec();

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
