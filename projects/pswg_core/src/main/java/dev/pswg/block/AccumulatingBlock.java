package dev.pswg.block;

import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class AccumulatingBlock extends Block
{
	public static final int maxTotalLayers = 8;
	public static final int maxPathfindingLayers = 5;

	public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
	protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[] {
			Shapes.empty(),
			Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
			Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	private final Function<BlockPlaceContext, BlockState> fullBlockState;

	public AccumulatingBlock(BlockBehaviour.Properties settings)
	{
		this(settings, null);
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
	}

	public AccumulatingBlock(BlockBehaviour.Properties settings, Function<BlockPlaceContext, BlockState> fullBlockState)
	{
		super(settings);
		this.fullBlockState = fullBlockState;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type)
	{
		if (type == PathComputationType.LAND)
			return state.getValue(LAYERS) < maxPathfindingLayers;

		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return LAYERS_TO_SHAPE[state.getValue(LAYERS)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return LAYERS_TO_SHAPE[state.getValue(LAYERS) - 1];
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos)
	{
		return LAYERS_TO_SHAPE[state.getValue(LAYERS)];
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return LAYERS_TO_SHAPE[state.getValue(LAYERS)];
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state)
	{
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		var blockState = world.getBlockState(pos.below());
		return Block.isFaceFull(blockState.getCollisionShape(world, pos.below()), Direction.UP) || blockState.is(this) && blockState.getValue(LAYERS) == maxTotalLayers;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random)
	{
		return !state.canSurvive(world, pos)
		       ? Blocks.AIR.defaultBlockState()
		       : super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context)
	{
		int i = state.getValue(LAYERS);
		if (context.getItemInHand().is(this.asItem()) && i < maxTotalLayers)
		{
			if (context.replacingClickedOnBlock())
				return context.getClickedFace() == Direction.UP;

			return true;
		}

		return i == 1;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
		if (blockState.is(this))
		{
			int i = blockState.getValue(LAYERS);
			if (i + 1 >= maxTotalLayers && fullBlockState != null)
				return fullBlockState.apply(ctx);

			return blockState.setValue(LAYERS, Math.min(maxTotalLayers, i + 1));
		}

		return super.getStateForPlacement(ctx);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(LAYERS);
	}
}
