package dev.pswg.block;

import dev.pswg.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClusterLightBlock extends WaterloggableRotatingBlockWithBounds implements IPicklingBlock
{
	public static final IntegerProperty CLUSTER_SIZE = IntegerProperty.create("cluster_size", 1, 3);

	private static final VoxelShape SHAPE_SINGLE = Shapes.box(0, 5.5f / 16, 5.5f / 16, 4 / 16f, 10.5f / 16, 10.5f / 16);
	private static final VoxelShape SHAPE_DOUBLE = Shapes.join(
			Shapes.box(0, 5.5f / 16, (5.5f - 7 + 3.5f) / 16, 4 / 16f, 10.5f / 16, (10.5f - 7 + 3.5f) / 16),
			Shapes.box(0, 5.5f / 16, (5.5f + 3.5f) / 16, 4 / 16f, 10.5f / 16, (10.5f + 3.5f) / 16),
			BooleanOp.OR
	);
	private static final VoxelShape SHAPE_TRIPLE = VoxelShapeUtil.union(
			Shapes.box(0, (5.5f + 3.5f) / 16, (5.5f - 7 + 3.5f) / 16, 4 / 16f, (10.5f + 3.5f) / 16, (10.5f - 7 + 3.5f) / 16),
			Shapes.box(0, (5.5f + 3.5f) / 16, (5.5f + 3.5f) / 16, 4 / 16f, (10.5f + 3.5f) / 16, (10.5f + 3.5f) / 16),
			Shapes.box(0, (5.5f - 3.5f) / 16, 5.5f / 16, 4 / 16f, (10.5f - 3.5f) / 16, 10.5f / 16)
	);

	public ClusterLightBlock(Substrate requiresSubstrate, BlockBehaviour.Properties settings)
	{
		super(null, requiresSubstrate, settings);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		var size = state.getValue(CLUSTER_SIZE);

		var shape = switch (size)
		{
			default -> SHAPE_SINGLE;
			case 2 -> SHAPE_DOUBLE;
			case 3 -> SHAPE_TRIPLE;
		};

		return VoxelShapeUtil.rotateToFace(shape, state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());

		if (blockState.is(this))
			return blockState.setValue(CLUSTER_SIZE, Math.min(3, blockState.getValue(CLUSTER_SIZE) + 1));

		return super.getPlacementStateBlockBased(ctx);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(CLUSTER_SIZE);
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext context)
	{
		return context.getItemInHand().is(this.asItem()) && state.getValue(CLUSTER_SIZE) < 3 || super.canBeReplaced(state, context);
	}

	@Override
	public IntegerProperty getPickleProperty()
	{
		return CLUSTER_SIZE;
	}
}