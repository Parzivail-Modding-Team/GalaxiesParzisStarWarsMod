package dev.pswg.block;

import dev.pswg.util.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DryingBlock extends FallingMutatingBlock
{
	public DryingBlock(Block target, int meanTransitionTime, Properties settings, ColorRGBA colorCode)
	{
		super(target, meanTransitionTime, settings, colorCode);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		return WorldUtil.isSunLit(world, pos);
	}

	@Override
	protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl)
	{
		entity.makeStuckInBlock(state, new Vec3(0.25, 1.5, 0.25));
		super.entityInside(state, world, pos, entity, handler, bl);
	}
}

