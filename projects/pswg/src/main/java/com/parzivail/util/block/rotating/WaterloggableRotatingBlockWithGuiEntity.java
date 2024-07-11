package com.parzivail.util.block.rotating;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class WaterloggableRotatingBlockWithGuiEntity extends WaterloggableRotatingBlockWithEntity
{
	private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityBiFunction;

	public WaterloggableRotatingBlockWithGuiEntity(Settings settings, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityBiFunction)
	{
		super(settings);
		this.blockEntityBiFunction = blockEntityBiFunction;
	}

	@Override
	protected MapCodec<? extends WaterloggableRotatingBlockWithGuiEntity> getCodec()
	{
		return super.getCodec();
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		if (!getBlockEntityUsePos(state, pos).equals(pos))
			return null;
		return blockEntityBiFunction.apply(pos, state);
	}

	protected BlockPos getBlockEntityUsePos(BlockState state, BlockPos pos)
	{
		return pos;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		if (world.isClient)
			return ActionResult.SUCCESS;
		else
		{
			player.openHandledScreen(state.createScreenHandlerFactory(world, getBlockEntityUsePos(state, pos)));
			return ActionResult.CONSUME;
		}
	}
}
