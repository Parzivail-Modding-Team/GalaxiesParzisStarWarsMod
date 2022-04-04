package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.LightsaberForgeBlockEntity;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.RotatingBlockWithGuiEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class LightsaberForgeBlock extends RotatingBlockWithGuiEntity
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

	public LightsaberForgeBlock(Settings settings)
	{
		super(settings, LightsaberForgeBlockEntity::new);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		var shape = VoxelShapes.union(
				VoxelShapes.cuboid(0, 10 / 16f, 0, 1, 13 / 16f, 1),
				VoxelShapes.cuboid(1 / 16f, 5 / 16f, 0, 15 / 16f, 7 / 16f, 10 / 16f),
				VoxelShapes.cuboid(1 / 16f, 0, 10 / 16f, 15 / 16f, 10 / 16f, 15 / 16f),
				VoxelShapes.cuboid(4f / 16f, 13 / 16f, 4f / 16f, 8f / 16f, 1, 1),
				VoxelShapes.cuboid(9f / 16f, 13 / 16f, 0, 13f / 16f, 14f / 16f, 6f / 16f),
				VoxelShapes.cuboid(9.5f / 16f, 13 / 16f, 4f / 16f, 12.5f / 16f, 15.75f / 16f, 5f / 16f)
		);
		return VoxelShapeUtil.rotate(shape, (state.get(FACING).getHorizontal() + 1) % 4);
	}
}
