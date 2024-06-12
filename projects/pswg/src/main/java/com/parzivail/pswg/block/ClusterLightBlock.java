package com.parzivail.pswg.block;

import com.mojang.serialization.MapCodec;
import com.parzivail.util.block.IPicklingBlock;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithBounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class ClusterLightBlock extends WaterloggableRotatingBlockWithBounds implements IPicklingBlock
{
	public static final IntProperty CLUSTER_SIZE = IntProperty.of("cluster_size", 1, 3);

	private static final VoxelShape SHAPE_SINGLE = VoxelShapes.cuboid(0, 5.5f / 16, 5.5f / 16, 4 / 16f, 10.5f / 16, 10.5f / 16);
	private static final VoxelShape SHAPE_DOUBLE = VoxelShapes.union(
			VoxelShapes.cuboid(0, 5.5f / 16, (5.5f - 7 + 3.5f) / 16, 4 / 16f, 10.5f / 16, (10.5f - 7 + 3.5f) / 16),
			VoxelShapes.cuboid(0, 5.5f / 16, (5.5f + 3.5f) / 16, 4 / 16f, 10.5f / 16, (10.5f + 3.5f) / 16)
	);
	private static final VoxelShape SHAPE_TRIPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0, (5.5f + 3.5f) / 16, (5.5f - 7 + 3.5f) / 16, 4 / 16f, (10.5f + 3.5f) / 16, (10.5f - 7 + 3.5f) / 16),
			VoxelShapes.cuboid(0, (5.5f + 3.5f) / 16, (5.5f + 3.5f) / 16, 4 / 16f, (10.5f + 3.5f) / 16, (10.5f + 3.5f) / 16),
			VoxelShapes.cuboid(0, (5.5f - 3.5f) / 16, 5.5f / 16, 4 / 16f, (10.5f - 3.5f) / 16, 10.5f / 16)
	);

	public ClusterLightBlock(Substrate requiresSubstrate, Settings settings)
	{
		super(null, requiresSubstrate, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(CLUSTER_SIZE, 1));
	}

	@Override
	protected MapCodec<ClusterLightBlock> getCodec()
	{
		return super.getCodec();
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		var size = state.get(CLUSTER_SIZE);

		var shape = switch (size)
				{
					default -> SHAPE_SINGLE;
					case 2 -> SHAPE_DOUBLE;
					case 3 -> SHAPE_TRIPLE;
				};

		return VoxelShapeUtil.rotateToFace(shape, state.get(FACING));
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());

		if (blockState.isOf(this))
			return blockState.with(CLUSTER_SIZE, Math.min(3, blockState.get(CLUSTER_SIZE) + 1));

		return super.getPlacementStateBlockBased(ctx);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(CLUSTER_SIZE);
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(CLUSTER_SIZE) < 3 || super.canReplace(state, context);
	}

	@Override
	protected IntProperty getPickleProperty()
	{
		return CLUSTER_SIZE;
	}
}
