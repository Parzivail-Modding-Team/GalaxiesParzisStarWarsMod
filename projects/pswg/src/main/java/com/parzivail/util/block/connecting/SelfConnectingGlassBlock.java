package com.parzivail.util.block.connecting;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SelfConnectingGlassBlock extends SelfConnectingBlock
{
	private static final MapCodec<SelfConnectingGlassBlock> CODEC = createCodec(SelfConnectingGlassBlock::new);

	public SelfConnectingGlassBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends SelfConnectingGlassBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}

	@Override
	protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
	{
		return true;
	}
}
