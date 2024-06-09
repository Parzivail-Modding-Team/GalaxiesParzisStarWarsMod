package com.parzivail.util.block.connecting;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
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
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
	{
		return true;
	}
}
