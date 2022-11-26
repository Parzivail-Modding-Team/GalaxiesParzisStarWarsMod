package com.parzivail.pswg.block;

import com.parzivail.util.block.DisplacingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Random;

public class DeshBarrelBlock extends DisplacingBlock
{
	private static final VoxelShape SHAPE;

	static
	{
		var shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.75, 0.21875, 0.78125, 0.9375, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.3125, 0.21875, 0.78125, 0.6875, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.0625, 0.21875, 0.78125, 0.25, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.28125, 0, 0.28125, 0.71875, 1, 0.71875));
		SHAPE = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.25, 0.75, 0.75, 0.75));
	}

	public DeshBarrelBlock(Settings settings)
	{
		super(DeshBarrelBlock::displaceShape, settings);
	}

	private static VoxelShape displaceShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext)
	{
		var r = new Random(blockState.getRenderingSeed(blockPos));

		var dx = r.nextFloat() / 4;
		var dz = r.nextFloat() / 4;

		return SHAPE.offset(dx, 0, dz);
	}
}
