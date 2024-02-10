package com.parzivail.util.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VerticalSlabBlock extends Block implements Waterloggable
{
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
	public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

	public VerticalSlabBlock(AbstractBlock.Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Y).with(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	public boolean hasSidedTransparency(BlockState state)
	{
		return state.get(TYPE) != SlabType.DOUBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, AXIS, WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		var slabType = state.get(TYPE);
		switch (state.get(AXIS))
		{
			case X:
				switch (slabType)
				{
					case TOP:
						return EAST_SHAPE;
					case BOTTOM:
						return WEST_SHAPE;
				}
			case Y:
				switch (slabType)
				{
					case TOP:
						return TOP_SHAPE;
					case BOTTOM:
						return BOTTOM_SHAPE;
				}
			case Z:
				switch (slabType)
				{
					case TOP:
						return SOUTH_SHAPE;
					case BOTTOM:
						return NORTH_SHAPE;
				}
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var blockPos = ctx.getBlockPos();
		var existingState = ctx.getWorld().getBlockState(blockPos);

		if (existingState.isOf(this))
			return existingState.with(TYPE, SlabType.DOUBLE).with(AXIS, existingState.get(AXIS)).with(WATERLOGGED, Boolean.FALSE);
		else
			return getEmptyPlacementState(ctx);
	}

	private BlockState getEmptyPlacementState(ItemPlacementContext ctx)
	{
		var blockPos = ctx.getBlockPos();

		var fluidState = ctx.getWorld().getFluidState(blockPos);
		var placingState = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Y).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

		var direction = ctx.getSide();
		var sneaking = ctx.getPlayer() != null && ctx.getPlayer().isSneaking();

		switch (direction)
		{
			case UP:
			case DOWN:
			{
				if (sneaking)
				{
					// Place vertical slab above or below block
					var playerLookDir = Direction.fromRotation(ctx.getPlayerYaw());

					var axis = playerLookDir.getAxis();
					var half = SlabType.BOTTOM;

					switch (axis)
					{
						case X -> half = (ctx.getHitPos().x - (double)blockPos.getX() > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
						case Z -> half = (ctx.getHitPos().z - (double)blockPos.getZ() > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
					}

					return placingState.with(TYPE, half).with(AXIS, axis);
				}
				else
				{
					if (direction == Direction.DOWN)
						return placingState.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Y);
					return placingState;
				}
			}
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
			{
				if (sneaking)
					return switch (direction)
					{
						case NORTH, WEST -> placingState.with(TYPE, SlabType.TOP).with(AXIS, direction.getAxis());
						case SOUTH, EAST -> placingState.with(TYPE, SlabType.BOTTOM).with(AXIS, direction.getAxis());
						default -> throw new RuntimeException("Impossible state");
					};
				else
					return ctx.getHitPos().y - (double)blockPos.getY() > 0.5 ? placingState.with(TYPE, SlabType.TOP) : placingState;
			}
			default:
				throw new RuntimeException("Impossible state");
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		var axis = state.get(AXIS);

		var existingState = context.getWorld().getBlockState(context.getBlockPos());
		if (!existingState.isOf(this) || state.get(TYPE) == SlabType.DOUBLE)
			return false;

		var possiblePlacement = getEmptyPlacementState(context);

		if (axis == Direction.Axis.Y && axis != possiblePlacement.get(AXIS))
			return false;

		if (state.get(TYPE) != possiblePlacement.get(TYPE))
			return true;

		return context.getSide().getAxis() == axis;
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		return state.get(TYPE) != SlabType.DOUBLE && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
	}

	@Override
	public boolean canFillWithFluid(@Nullable PlayerEntity player, BlockView world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return state.get(TYPE) != SlabType.DOUBLE && Waterloggable.super.canFillWithFluid(player, world, pos, state, fluid);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		if (state.get(WATERLOGGED))
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		switch (type)
		{
			case WATER:
				return world.getFluidState(pos).isIn(FluidTags.WATER);
			default:
				return false;
		}
	}
}

