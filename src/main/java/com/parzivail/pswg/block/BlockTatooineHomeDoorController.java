package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.util.block.VoxelShapeUtil;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlockTatooineHomeDoorController extends BlockTatooineHomeDoor implements BlockEntityProvider
{
	private static final VoxelShape SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0, 0, 0.25, 1, 0.0625, 0.75),
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(0.0625, 0.0625, 0.375, 1 - 0.0625, 1, 0.625));
	private static final VoxelShape SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0, 0, 0.25, 1, 0.0625, 0.75),
			VoxelShapes.cuboid(0, 0, 0.25, 0.0625, 1, 0.75),
			VoxelShapes.cuboid(1 - 0.0625, 0, 0.25, 1, 1, 0.75),
			VoxelShapes.cuboid(1 - 1.5 * 0.0625, 0.0625, 0.375, 1 - 0.0625, 1, 0.625));

	private static final VoxelShape[] SHAPES_CLOSED = new VoxelShape[4];
	private static final VoxelShape[] SHAPES_OPEN = new VoxelShape[4];

	static
	{
		for (int i = 0; i < 4; i++)
		{
			SHAPES_CLOSED[i] = VoxelShapeUtil.rotate(SHAPE_CLOSED, i);
			SHAPES_OPEN[i] = VoxelShapeUtil.rotate(SHAPE_OPEN, i);
		}
	}

	public BlockTatooineHomeDoorController(Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		BlockPos controllerPos = getController(world, pos);
		TatooineHomeDoorBlockEntity e = (TatooineHomeDoorBlockEntity)world.getBlockEntity(controllerPos);

		int rotation = (state.get(ROTATION) + 3) % 4;

		if (e == null || !e.isOpening() || e.isMoving())
			return SHAPES_OPEN[rotation];

		return SHAPES_CLOSED[rotation];
	}

	@Override
	protected BlockPos getController(BlockView world, BlockPos self)
	{
		return self;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new TatooineHomeDoorBlockEntity();
	}
}
