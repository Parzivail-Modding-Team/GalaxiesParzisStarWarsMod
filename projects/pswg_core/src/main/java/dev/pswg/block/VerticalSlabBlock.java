package dev.pswg.block;

import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock
{
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape TOP_SHAPE = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_SHAPE = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

	public VerticalSlabBlock(BlockBehaviour.Properties settings)
	{
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(AXIS, Direction.Axis.Y).setValue(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, AXIS, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		var slabType = state.getValue(TYPE);
		var axis = state.getValue(AXIS);
		return switch (slabType)
		{
			case DOUBLE -> Shapes.block();
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
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var blockPos = ctx.getClickedPos();
		var existingState = ctx.getLevel().getBlockState(blockPos);

		if (existingState.is(this))
			return existingState.setValue(TYPE, SlabType.DOUBLE).setValue(AXIS, existingState.getValue(AXIS)).setValue(WATERLOGGED, Boolean.FALSE);
		else
			return getEmptyPlacementState(ctx);
	}

	private BlockState getEmptyPlacementState(BlockPlaceContext ctx)
	{
		var blockPos = ctx.getClickedPos();

		var fluidState = ctx.getLevel().getFluidState(blockPos);
		var placingState = this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(AXIS, Direction.Axis.Y).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

		var direction = ctx.getClickedFace();
		var sneaking = ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown();

		var playerLookDir = ctx.getNearestLookingDirection();
		var playerLookAxis = playerLookDir.getAxis();

		if (sneaking)
		{
			// Place vertical slab above or below block
			var half = SlabType.BOTTOM;

			switch (playerLookAxis)
			{
				case X -> half = (ctx.getClickLocation().x - (double)blockPos.getX() > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
				case Z -> half = (ctx.getClickLocation().z - (double)blockPos.getZ() > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
			}

			return placingState.setValue(TYPE, half).setValue(AXIS, playerLookAxis);
		}
		else
		{
			return switch (direction)
			{
				case UP, DOWN ->
				{
					if (direction == Direction.DOWN)
						yield placingState.setValue(TYPE, SlabType.TOP).setValue(AXIS, Direction.Axis.Y);
					yield placingState;
				}
				case NORTH, SOUTH, EAST, WEST -> ctx.getClickLocation().y - (double)blockPos.getY() > 0.5 ? placingState.setValue(TYPE, SlabType.TOP) : placingState;
			};
		}
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context)
	{
		var itemStack = context.getItemInHand();
		var slabType = state.getValue(TYPE);

		var axis = state.getValue(AXIS);
		var direction = context.getClickedFace();

		if (slabType == SlabType.DOUBLE || !itemStack.is(this.asItem()))
			return false;

		if (!context.replacingClickedOnBlock())
			return true;

		switch (axis)
		{
			case X:
			{
				boolean isUpperHalf = context.getClickLocation().x - context.getClickedPos().getX() > 0.5;

				if (slabType == SlabType.BOTTOM)
					return direction == Direction.EAST || (isUpperHalf && direction.getAxis().isVertical());
				else
					return direction == Direction.WEST || (!isUpperHalf && direction.getAxis().isVertical());
			}
			case Y:
			{
				boolean isUpperHalf = context.getClickLocation().y - context.getClickedPos().getY() > 0.5;

				if (slabType == SlabType.BOTTOM)
					return direction == Direction.UP || (isUpperHalf && direction.getAxis().isHorizontal());
				else
					return direction == Direction.DOWN || (!isUpperHalf && direction.getAxis().isHorizontal());
			}
			case Z:
			{
				boolean isUpperHalf = context.getClickLocation().z - context.getClickedPos().getZ() > 0.5;

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
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE && SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluidState);
	}

	@Override
	public boolean canPlaceLiquid(@Nullable LivingEntity filler, BlockGetter world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return state.getValue(TYPE) != SlabType.DOUBLE && SimpleWaterloggedBlock.super.canPlaceLiquid(filler, world, pos, state, fluid);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random)
	{
		if (state.getValue(WATERLOGGED))
			tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type)
	{
		switch (type)
		{
			case WATER:
				return state.getFluidState().is(FluidTags.WATER);
			default:
				return false;
		}
	}
}
