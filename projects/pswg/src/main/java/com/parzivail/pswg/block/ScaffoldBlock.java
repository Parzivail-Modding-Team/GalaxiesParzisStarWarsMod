package com.parzivail.pswg.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class ScaffoldBlock extends ScaffoldingBlock
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

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return calculateOverriddenDistance(world, pos) < 7;
	}

	private int calculateOverriddenDistance(BlockView world, BlockPos pos)
	{
		var mutable = pos.mutableCopy().move(Direction.DOWN);
		var blockState = world.getBlockState(mutable);
		var i = 7;
		if (blockState.isOf(this))
			i = blockState.get(DISTANCE);
		else if (blockState.isSideSolidFullSquare(world, mutable, Direction.UP))
			return 0;

		for (var direction : Direction.Type.HORIZONTAL)
		{
			var blockState2 = world.getBlockState(mutable.set(pos, direction));
			if (blockState2.isOf(this))
			{
				i = Math.min(i, blockState2.get(DISTANCE) + 1);
				if (i == 1)
					break;
			}
		}

		return i;
	}

	private static VoxelShape getShape()
	{
		var shape = VoxelShapes.empty();
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
