package com.parzivail.util.block.connecting;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public abstract class DelegatedConnectingBlock extends ConnectingBlock
{
	protected DelegatedConnectingBlock(AbstractBlock.Settings settings)
	{
		super(0.5f, settings);
		this.setDefaultState(
				this.stateManager.getDefaultState()
				                 .with(NORTH, false)
				                 .with(EAST, false)
				                 .with(SOUTH, false)
				                 .with(WEST, false)
				                 .with(UP, false)
				                 .with(DOWN, false)
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
	}

	public BlockState withConnectionProperties(BlockView world, BlockPos pos)
	{
		var self = this.getDefaultState();
		return this.getDefaultState()
		           .with(DOWN, shouldConnectTo(self, world.getBlockState(pos.down())))
		           .with(UP, shouldConnectTo(self, world.getBlockState(pos.up())))
		           .with(NORTH, shouldConnectTo(self, world.getBlockState(pos.north())))
		           .with(EAST, shouldConnectTo(self, world.getBlockState(pos.east())))
		           .with(SOUTH, shouldConnectTo(self, world.getBlockState(pos.south())))
		           .with(WEST, shouldConnectTo(self, world.getBlockState(pos.west())));
	}

	protected abstract boolean shouldConnectTo(BlockState self, BlockState other);

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
	{
		if (!state.canPlaceAt(world, pos))
		{
			world.createAndScheduleBlockTick(pos, this, 1);
			return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
		}
		else
		{
			// TODO: connections don't always work
			var bl = shouldConnectTo(state, newState);
			return state.with(FACING_PROPERTIES.get(direction), bl);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
}
