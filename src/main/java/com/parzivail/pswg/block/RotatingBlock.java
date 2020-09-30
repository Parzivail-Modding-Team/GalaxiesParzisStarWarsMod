package com.parzivail.pswg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.MathHelper;

public class RotatingBlock extends Block
{
	static
	{
		ROTATION = Properties.ROTATION;
	}

	public static final IntProperty ROTATION;

	public RotatingBlock(Settings settings)
	{
		super(settings);
	}

	public float getRotationDegrees(BlockState state)
	{
		if (!state.contains(ROTATION))
			return 0;

		int rotationIdx = state.get(ROTATION);

		return -rotationIdx * 45;
	}

	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(ROTATION, MathHelper.floor((double)((ctx.getPlayerYaw() - 90) * 8.0F / 360.0F) + 0.5D) & 7);
	}

	public BlockState rotate(BlockState state, BlockRotation rotation)
	{
		return state.with(ROTATION, rotation.rotate(state.get(ROTATION), 8));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.with(ROTATION, mirror.mirror(state.get(ROTATION), 8));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(ROTATION);
	}
}
