package com.parzivail.util.block.rotating;

import com.mojang.serialization.MapCodec;
import com.parzivail.util.block.WaterloggableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

public class WaterloggableRotatingBlock extends WaterloggableBlock
{
	private static final MapCodec<WaterloggableRotatingBlock> CODEC = createCodec(WaterloggableRotatingBlock::new);
	public static final DirectionProperty FACING = Properties.FACING;

	public WaterloggableRotatingBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends WaterloggableRotatingBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return super.getPlacementState(ctx).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	protected BlockState getPlacementStateBlockBased(ItemPlacementContext ctx)
	{
		return super.getPlacementState(ctx).with(FACING, ctx.getSide());
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation)
	{
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(FACING);
	}
}
