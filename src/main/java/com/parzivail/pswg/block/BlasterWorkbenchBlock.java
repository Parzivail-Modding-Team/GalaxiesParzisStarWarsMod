package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.BlasterWorkbenchBlockEntity;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithGuiEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlasterWorkbenchBlock extends WaterloggableRotatingBlockWithGuiEntity
{
	private static final VoxelShape SHAPE = VoxelShapes.union(
			VoxelShapes.cuboid(0, 10 / 16f, 0, 1, 13 / 16f, 1),
			VoxelShapes.cuboid(1 / 16f, 5 / 16f, 0, 15 / 16f, 7 / 16f, 10 / 16f),
			VoxelShapes.cuboid(1 / 16f, 0, 10 / 16f, 15 / 16f, 10 / 16f, 15 / 16f),
			VoxelShapes.cuboid(0, 13 / 16f, 0.4f / 16f, 5.25f / 16f, 1, 8.5f / 16f),
			VoxelShapes.cuboid(2.5f / 16f, 13 / 16f, 11.5f / 16f, 5.5f / 16f, 15.75f / 16f, 14.5f / 16f)
	);
	private static final VoxelShape[] SHAPE_ROTATIONS = new VoxelShape[4];

	static
	{
		for (var i = 0; i < 4; i++)
			SHAPE_ROTATIONS[i] = VoxelShapeUtil.rotate(SHAPE, i);
	}

	public BlasterWorkbenchBlock(Settings settings)
	{
		super(settings, BlasterWorkbenchBlockEntity::new);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE_ROTATIONS[(state.get(FACING).getHorizontal() + 1) % 4];
	}
}
