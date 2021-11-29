package com.parzivail.util.block.rotating;

import com.parzivail.util.block.VoxelShapeUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RotatingBlockWithBounds extends RotatingBlock
{
	public enum Substrate
	{
		BEHIND,
		BELOW,
		NONE
	}

	private final VoxelShape shape;
	private final Substrate requiresSubstrate;

	public RotatingBlockWithBounds(VoxelShape shape, Substrate requiresSubstrate, Settings settings)
	{
		super(settings.dynamicBounds());
		this.shape = shape;
		this.requiresSubstrate = requiresSubstrate;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		// East isn't zero, but everything defaults to facing east
		return VoxelShapeUtil.rotate(shape, (state.get(FACING).getHorizontal() + 1) % 4);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		if (requiresSubstrate == Substrate.NONE)
			return super.canPlaceAt(state, world, pos);

		var substrateDirection = getSubstrateDirection(state);

		switch (requiresSubstrate)
		{
			case BEHIND -> {
				BlockPos blockPos = pos.offset(substrateDirection);
				return sideCoversSmallSquare(world, blockPos, substrateDirection.getOpposite());
			}
			case BELOW -> {
				BlockPos blockPos = pos.down();
				return hasTopRim(world, blockPos) || sideCoversSmallSquare(world, blockPos, Direction.UP);
			}
			default -> throw new IllegalStateException("Unexpected value: " + requiresSubstrate);
		}
	}

	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
	{
		if (state.get(Properties.WATERLOGGED))
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		if (requiresSubstrate == Substrate.NONE)
			return state;

		var substrateDirection = getSubstrateDirection(state);

		return direction.getOpposite() == substrateDirection && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
	}

	private Direction getSubstrateDirection(BlockState state)
	{
		if (requiresSubstrate == Substrate.BELOW)
			return Direction.DOWN;

		return state.get(FACING).getOpposite();
	}
}
