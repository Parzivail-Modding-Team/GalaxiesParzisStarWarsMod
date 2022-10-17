package com.parzivail.pswg.block;

import com.parzivail.util.block.WaterloggableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ScaffoldBlock extends WaterloggableBlock
{
	private static final VoxelShape SHAPE = getShape();

	public ScaffoldBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	private static VoxelShape getShape()
	{
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.125, 1, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.875, 0.125, 1, 1));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0, 1, 1, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0.875, 1, 1, 1));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.875, 0.125, 0.125, 0.9375, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.875, 0.125, 0.9375, 0.9375, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.875, 0.0625, 0.875, 0.9375, 0.125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.875, 0.875, 0.875, 0.9375, 0.9375));
		return shape;
	}
}
