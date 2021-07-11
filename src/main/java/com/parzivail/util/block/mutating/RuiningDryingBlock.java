package com.parzivail.util.block.mutating;

import com.google.common.base.Suppliers;
import com.parzivail.util.world.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public class RuiningDryingBlock extends MutatingBlock
{
	private final Supplier<Block> ruinedBlock;

	public RuiningDryingBlock(Block target, int meanTransitionTime, Supplier<Block> ruinedBlock, Settings settings)
	{
		super(target, meanTransitionTime, settings);
		this.ruinedBlock = Suppliers.memoize(ruinedBlock::get);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return WorldUtil.isSunLit(world, pos);
	}

	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
	{
		if (!world.isClient && world.random.nextFloat() < fallDistance - 0.5F && entity instanceof LivingEntity && (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F)
			world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(state, ruinedBlock.get().getDefaultState(), world, pos));

		super.onLandedUpon(world, state, pos, entity, fallDistance);
	}
}
