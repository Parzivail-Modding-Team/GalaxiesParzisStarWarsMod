package com.parzivail.util.block.mutating;

import com.parzivail.util.world.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class DryingSlabBlock extends MutatingSlabBlock
{
	public DryingSlabBlock(SlabBlock target, int meanTransitionTime, Settings settings)
	{
		super(target, meanTransitionTime, settings);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return WorldUtil.isSunLit(world, pos);
	}
}
