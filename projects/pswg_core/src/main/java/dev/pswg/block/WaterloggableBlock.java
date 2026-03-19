package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class WaterloggableBlock extends Block implements SimpleWaterloggedBlock
{
	public WaterloggableBlock(Properties settings)
	{
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random)
	{
		if (state.getValue(BlockStateProperties.WATERLOGGED))
			tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

		return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		return super.getStateForPlacement(ctx).setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}
}
