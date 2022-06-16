package com.parzivail.util.block.mutating;

import com.parzivail.util.block.VerticalSlabBlock;
import com.parzivail.util.world.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DryingSlabBlock extends MutatingSlabBlock
{
	public DryingSlabBlock(VerticalSlabBlock target, int meanTransitionTime, Settings settings)
	{
		super(target, meanTransitionTime, settings);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return WorldUtil.isSunLit(world, pos);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		entity.slowMovement(state, new Vec3d(0.25, 1.5, 0.25));
		super.onEntityCollision(state, world, pos, entity);
	}
}
