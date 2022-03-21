package com.parzivail.util.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AccessibleMetalTrapdoorBlock extends TrapdoorBlock
{
	public AccessibleMetalTrapdoorBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		state = state.cycle(OPEN);
		world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
		if (state.get(WATERLOGGED))
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		this.playToggleSound(player, world, pos, state.get(OPEN));
		return ActionResult.success(world.isClient);
	}
}
