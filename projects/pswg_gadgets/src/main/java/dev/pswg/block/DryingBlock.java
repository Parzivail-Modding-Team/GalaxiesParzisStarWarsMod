package dev.pswg.block;

import dev.pswg.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DryingBlock extends FallingMutatingBlock
{
	public DryingBlock(Block target, int meanTransitionTime, Settings settings, ColorCode colorCode)
	{
		super(target, meanTransitionTime, settings, colorCode);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return WorldUtil.isSunLit(world, pos);
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl)
	{
		entity.slowMovement(state, new Vec3d(0.25, 1.5, 0.25));
		super.onEntityCollision(state, world, pos, entity, handler, bl);
	}
}

