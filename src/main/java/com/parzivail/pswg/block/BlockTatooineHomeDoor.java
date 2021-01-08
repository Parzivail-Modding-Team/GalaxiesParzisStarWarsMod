package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.util.block.VoxelShapeUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockTatooineHomeDoor extends RotatingBlockWithEntity
{
	private final VoxelShape shape;

	public BlockTatooineHomeDoor(Settings settings)
	{
		super(settings);

		shape = VoxelShapeUtil.getCenteredCube(10, 40);
	}

	//	@Override
	//	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	//	{
	//		return shape;
	//	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new TatooineHomeDoorBlockEntity();
	}
}
