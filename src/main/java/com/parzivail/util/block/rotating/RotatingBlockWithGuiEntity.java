package com.parzivail.util.block.rotating;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class RotatingBlockWithGuiEntity extends RotatingBlockWithEntity
{
	private final Supplier<BlockEntity> blockEntitySupplier;

	public RotatingBlockWithGuiEntity(Settings settings, Supplier<BlockEntity> blockEntitySupplier)
	{
		super(settings);
		this.blockEntitySupplier = blockEntitySupplier;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return blockEntitySupplier.get();
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
