package com.parzivail.util.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class AccumulatingBlock extends Block
{
	public static final int maxTotalLayers = 8;
	public static final int maxPathfindingLayers = 5;

	public static final IntProperty LAYERS = Properties.LAYERS;
	protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[] {
			VoxelShapes.empty(),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	private final Function<ItemPlacementContext, BlockState> fullBlockState;

	public AccumulatingBlock(AbstractBlock.Settings settings)
	{
		this(settings, null);
		this.setDefaultState(this.stateManager.getDefaultState().with(LAYERS, 1));
	}

	public AccumulatingBlock(AbstractBlock.Settings settings, Function<ItemPlacementContext, BlockState> fullBlockState)
	{
		super(settings);
		this.fullBlockState = fullBlockState;
	}

	@Override
	protected MapCodec<? extends AccumulatingBlock> getCodec()
	{
		return super.getCodec();
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type)
	{
		if (type == NavigationType.LAND)
			return state.get(LAYERS) < maxPathfindingLayers;

		return false;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return LAYERS_TO_SHAPE[state.get(LAYERS)];
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return LAYERS_TO_SHAPE[state.get(LAYERS) - 1];
	}

	@Override
	protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos)
	{
		return LAYERS_TO_SHAPE[state.get(LAYERS)];
	}

	@Override
	protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return LAYERS_TO_SHAPE[state.get(LAYERS)];
	}

	@Override
	protected boolean hasSidedTransparency(BlockState state)
	{
		return true;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		var blockState = world.getBlockState(pos.down());
		return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos.down()), Direction.UP) || blockState.isOf(this) && blockState.get(LAYERS) == maxTotalLayers;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		return !state.canPlaceAt(world, pos)
		       ? Blocks.AIR.getDefaultState()
		       : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		int i = state.get(LAYERS);
		if (context.getStack().isOf(this.asItem()) && i < maxTotalLayers)
		{
			if (context.canReplaceExisting())
				return context.getSide() == Direction.UP;

			return true;
		}

		return i == 1;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (blockState.isOf(this))
		{
			int i = blockState.get(LAYERS);
			if (i + 1 >= maxTotalLayers && fullBlockState != null)
				return fullBlockState.apply(ctx);

			return blockState.with(LAYERS, Math.min(maxTotalLayers, i + 1));
		}

		return super.getPlacementState(ctx);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(LAYERS);
	}
}
