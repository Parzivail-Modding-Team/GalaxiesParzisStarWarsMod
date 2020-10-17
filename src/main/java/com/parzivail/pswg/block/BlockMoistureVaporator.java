package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.MoistureVaporatorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BlockMoistureVaporator extends RotatingBlockWithEntity implements IMoistureProvider
{
	public BlockMoistureVaporator(Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean providesMoisture(WorldView world, BlockPos blockPos, BlockState blockState)
	{
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new MoistureVaporatorBlockEntity();
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
