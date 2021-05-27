package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.LightsaberForgeBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightsaberForgeBlock extends BlockWithEntity
{
	public LightsaberForgeBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new LightsaberForgeBlockEntity();
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (world.isClient)
			return ActionResult.SUCCESS;
		else
		{
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			return ActionResult.CONSUME;
		}
	}
}
