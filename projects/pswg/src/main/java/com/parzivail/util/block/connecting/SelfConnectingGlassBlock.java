package com.parzivail.util.block.connecting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SelfConnectingGlassBlock extends SelfConnectingBlock
{
	public SelfConnectingGlassBlock(Settings settings)
	{
		super(settings);
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
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos)
	{
		return true;
	}
}
