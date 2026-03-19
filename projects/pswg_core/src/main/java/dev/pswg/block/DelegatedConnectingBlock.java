package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class DelegatedConnectingBlock extends PipeBlock
{
	protected DelegatedConnectingBlock(BlockBehaviour.Properties settings)
	{
		super(0.5f, settings);
		this.registerDefaultState(
				this.stateDefinition.any()
				                 .setValue(NORTH, false)
				                 .setValue(EAST, false)
				                 .setValue(SOUTH, false)
				                 .setValue(WEST, false)
				                 .setValue(UP, false)
				                 .setValue(DOWN, false)
		);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return this.withConnectionProperties(ctx.getLevel(), ctx.getClickedPos());
	}

	public BlockState withConnectionProperties(BlockGetter world, BlockPos pos)
	{
		var self = this.defaultBlockState();
		return this.defaultBlockState()
		           .setValue(DOWN, shouldConnectTo(self, world.getBlockState(pos.below())))
		           .setValue(UP, shouldConnectTo(self, world.getBlockState(pos.above())))
		           .setValue(NORTH, shouldConnectTo(self, world.getBlockState(pos.north())))
		           .setValue(EAST, shouldConnectTo(self, world.getBlockState(pos.east())))
		           .setValue(SOUTH, shouldConnectTo(self, world.getBlockState(pos.south())))
		           .setValue(WEST, shouldConnectTo(self, world.getBlockState(pos.west())));
	}

	protected abstract boolean shouldConnectTo(BlockState self, BlockState other);

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random)
	{
		if (!state.canSurvive(world, pos))
		{
			tickView.scheduleTick(pos, this, 1);
			return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
		}
		else
		{
			// TODO: connections don't always work
			var bl = shouldConnectTo(state, neighborState);
			return state.setValue(PROPERTY_BY_DIRECTION.get(direction), bl);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
}
