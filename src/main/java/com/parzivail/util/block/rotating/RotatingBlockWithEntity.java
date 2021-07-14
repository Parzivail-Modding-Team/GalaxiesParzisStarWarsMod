package com.parzivail.util.block.rotating;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class RotatingBlockWithEntity extends RotatingBlock implements BlockEntityProvider
{
	protected RotatingBlockWithEntity(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data)
	{
		super.onSyncedBlockEvent(state, world, pos, type, data);
		var blockEntity = world.getBlockEntity(pos);
		return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
	}

	@Nullable
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos)
	{
		var blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)blockEntity : null;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (!state.isOf(newState.getBlock()))
		{
			var blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory)
			{
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}
}
