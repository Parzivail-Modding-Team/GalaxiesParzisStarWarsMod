package com.parzivail.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.function.Supplier;

public class RotatingBlockWithBoundsGuiEntity extends RotatingBlockWithGuiEntity
{
	private final VoxelShape shape;

	public RotatingBlockWithBoundsGuiEntity(VoxelShape shape, Settings settings, Supplier<BlockEntity> blockEntitySupplier)
	{
		super(settings.dynamicBounds(), blockEntitySupplier);
		this.shape = shape;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapeUtil.rotate(shape, state.get(ROTATION) % 4);
	}
}
