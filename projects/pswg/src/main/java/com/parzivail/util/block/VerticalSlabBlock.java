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
		var blockState = ctx.getWorld().getBlockState(blockPos);
		if (blockState.isOf(this))
		{
			return blockState.with(TYPE, SlabType.DOUBLE).with(AXIS, blockState.get(AXIS)).with(WATERLOGGED, Boolean.FALSE);
		}
		else
		{
			var fluidState = ctx.getWorld().getFluidState(blockPos);
			var blockState2 = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Y).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

			var direction = ctx.getSide();

			if (ctx.getPlayer() != null && ctx.getPlayer().isSneaking())
			{
				if (direction != Direction.DOWN && direction != Direction.UP)
				{
					// Place horizontal slab adjacent to a block
					return ctx.getHitPos().y - (double)blockPos.getY() > 0.5 ? blockState2.with(TYPE, SlabType.TOP) : blockState2;
				}
				else
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

					return this.getDefaultState().with(TYPE, half).with(AXIS, axis).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
				}
			}
			else
			{
				return switch (direction)
						{
							case NORTH -> blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Z);
							case SOUTH -> blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Z);
							case EAST -> blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.X);
							case WEST -> blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.X);
							case DOWN -> blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Y);
							default -> blockState2;
						};
			}
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		var itemStack = context.getStack();
		var slabType = state.get(TYPE);
		var axis = state.get(AXIS);
		if (itemStack.getItem() == (this).asItem())
		{
			var direction = context.getSide();

			if (axis == Direction.Axis.Y)
			{
				return switch (slabType)
						{
							case BOTTOM -> direction == Direction.UP;
							case TOP -> direction == Direction.DOWN;
							default -> false;
						};
			}
			if (axis == Direction.Axis.X)
			{
				return switch (slabType)
						{
							case BOTTOM -> direction == Direction.EAST;
							case TOP -> direction == Direction.WEST;
							default -> false;
						};
			}
			if (axis == Direction.Axis.Z)
			{
				return switch (slabType)
						{
							case BOTTOM -> direction == Direction.SOUTH;
							case TOP -> direction == Direction.NORTH;
							default -> false;
						};
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
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

