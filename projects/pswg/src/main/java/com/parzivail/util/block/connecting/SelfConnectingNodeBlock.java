package com.parzivail.util.block.connecting;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SelfConnectingNodeBlock extends ConnectingNodeBlock
{
	private static final MapCodec<SelfConnectingNodeBlock> CODEC = createCodec(SelfConnectingNodeBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2, 2, 2, 14, 14, 14);

	public SelfConnectingNodeBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<SelfConnectingNodeBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	protected boolean canConnectTo(WorldAccess world, BlockState state, BlockState otherState, BlockPos otherPos, Direction direction)
	{
		return otherState.getBlock() == this;
	}

	@Override
	protected boolean isConnectedTo(WorldAccess world, BlockState state, BlockState otherState, BlockPos otherPos, Direction direction)
	{
		if (!canConnectTo(world, state, otherState, otherPos, direction))
			return false;

		//		if (state.get(FACING_PROPERTIES.get(direction)))
		//			return true;

		return otherState.get(FACING_PROPERTIES.get(direction.getOpposite()));
	}
}
