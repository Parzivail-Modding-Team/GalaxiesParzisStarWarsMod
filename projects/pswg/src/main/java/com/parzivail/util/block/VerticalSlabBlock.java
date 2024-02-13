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
		var axis = state.get(AXIS);
		return switch (slabType)
		{
			case DOUBLE -> VoxelShapes.fullCube();
			case TOP -> switch (axis)
			{
				case X -> EAST_SHAPE;
				case Y -> TOP_SHAPE;
				case Z -> SOUTH_SHAPE;
			};
			default -> switch (axis)
			{
				case X -> WEST_SHAPE;
				case Y -> BOTTOM_SHAPE;
				case Z -> NORTH_SHAPE;
			};
		};
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

		var playerLookDir = ctx.getPlayerLookDirection();
		var playerLookAxis = playerLookDir.getAxis();

		if (sneaking)
		{
			// Place vertical slab above or below block
			var half = SlabType.BOTTOM;

			switch (playerLookAxis)
			{
				case X -> half = (ctx.getHitPos().x - (double)blockPos.getX() > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
				case Z -> half = (ctx.getHitPos().z - (double)blockPos.getZ() > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
			}

			return placingState.with(TYPE, half).with(AXIS, playerLookAxis);
		}
		else
		{
			return switch (direction)
			{
				case UP, DOWN ->
				{
					if (direction == Direction.DOWN)
						yield placingState.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Y);
					yield placingState;
				}
				case NORTH, SOUTH, EAST, WEST -> ctx.getHitPos().y - (double)blockPos.getY() > 0.5 ? placingState.with(TYPE, SlabType.TOP) : placingState;
			};
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		var itemStack = context.getStack();
		var slabType = state.get(TYPE);

		var axis = state.get(AXIS);
		var direction = context.getSide();

		if (slabType == SlabType.DOUBLE || !itemStack.isOf(this.asItem()))
			return false;

		if (!context.canReplaceExisting())
			return true;

		switch (axis)
		{
			case X:
			{
				boolean isUpperHalf = context.getHitPos().x - context.getBlockPos().getX() > 0.5;

				if (slabType == SlabType.BOTTOM)
					return direction == Direction.EAST || (isUpperHalf && direction.getAxis().isVertical());
				else
					return direction == Direction.WEST || (!isUpperHalf && direction.getAxis().isVertical());
			}
			case Y:
			{
				boolean isUpperHalf = context.getHitPos().y - context.getBlockPos().getY() > 0.5;

				if (slabType == SlabType.BOTTOM)
					return direction == Direction.UP || (isUpperHalf && direction.getAxis().isHorizontal());
				else
					return direction == Direction.DOWN || (!isUpperHalf && direction.getAxis().isHorizontal());
			}
			case Z:
			{
				boolean isUpperHalf = context.getHitPos().z - context.getBlockPos().getZ() > 0.5;

				if (slabType == SlabType.BOTTOM)
					return direction == Direction.SOUTH || (isUpperHalf && direction.getAxis().isVertical());
				else
					return direction == Direction.NORTH || (!isUpperHalf && direction.getAxis().isVertical());
			}
			default:
				throw new RuntimeException("Impossible state");
		}
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
