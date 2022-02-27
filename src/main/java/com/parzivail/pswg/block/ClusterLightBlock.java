package com.parzivail.pswg.block;

import com.parzivail.util.block.IPicklingBlock;
import com.parzivail.util.block.rotating.RotatingBlockWithBounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ClusterLightBlock extends RotatingBlockWithBounds implements IPicklingBlock
{
	public static final IntProperty CLUSTER_SIZE = IntProperty.of("cluster_size", 1, 3);

	public ClusterLightBlock(VoxelShape shape, Substrate requiresSubstrate, Settings settings)
	{
		super(shape, requiresSubstrate, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(CLUSTER_SIZE, 1));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());

		if (blockState.isOf(this))
			return blockState.with(CLUSTER_SIZE, Math.min(3, blockState.get(CLUSTER_SIZE) + 1));

		return super.getPlacementState(ctx);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(CLUSTER_SIZE);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(CLUSTER_SIZE) < 3 || super.canReplace(state, context);
	}

	@Override
	public IntProperty getPickleProperty()
	{
		return CLUSTER_SIZE;
	}
}
