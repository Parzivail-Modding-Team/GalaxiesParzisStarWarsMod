package com.parzivail.util.block.mutating;

import com.google.common.base.Suppliers;
import com.parzivail.util.block.VerticalSlabBlock;
import com.parzivail.util.world.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class RuiningDryingSlabBlock extends MutatingSlabBlock
{
	private final Supplier<VerticalSlabBlock> ruinedBlock;

	public RuiningDryingSlabBlock(VerticalSlabBlock target, int meanTransitionTime, Supplier<VerticalSlabBlock> ruinedBlock, Settings settings)
	{
		super(target, meanTransitionTime, settings);
		this.ruinedBlock = Suppliers.memoize(ruinedBlock::get);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		return WorldUtil.isSunLit(world, pos);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if (!world.isClient && entity instanceof LivingEntity && (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F)
			world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(state, ruinedBlock.get().getStateWithProperties(state), world, pos));

		super.onEntityCollision(state, world, pos, entity);
	}
}
