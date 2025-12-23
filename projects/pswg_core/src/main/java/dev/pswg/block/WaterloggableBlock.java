package dev.pswg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class WaterloggableBlock extends Block implements Waterloggable
{
	public WaterloggableBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(Properties.WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(Properties.WATERLOGGED);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random)
	{
		if (state.get(Properties.WATERLOGGED))
			tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return super.getPlacementState(ctx).with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}
}
