package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.VoxelShapeUtil;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockTatooineHomeDoorController extends BlockTatooineHomeDoor implements BlockEntityProvider
{
	private static final VoxelShape INTERACTION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625),
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.0625, 1)
	);
	private static final VoxelShape INTERACTION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 1 - 1.5 * 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625),
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.0625, 1)
	);
	private static final VoxelShape COLLISION_SHAPE_CLOSED = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625)
	);
	private static final VoxelShape COLLISION_SHAPE_OPEN = VoxelShapes.union(
			VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.0625),
			VoxelShapes.cuboid(0.25, 0, 1 - 0.0625, 0.75, 1, 1),
			VoxelShapes.cuboid(0.375, 0, 1 - 1.5 * 0.0625, 0.625, 1 - 0.0625, 1 - 0.0625)
	);

	private static final VoxelShape[] INTERACTION_SHAPES_CLOSED = new VoxelShape[4];
	private static final VoxelShape[] INTERACTION_SHAPES_OPEN = new VoxelShape[4];
	private static final VoxelShape[] COLLISION_SHAPES_CLOSED = new VoxelShape[4];
	private static final VoxelShape[] COLLISION_SHAPES_OPEN = new VoxelShape[4];

	static
	{
		for (var i = 0; i < 4; i++)
		{
			INTERACTION_SHAPES_CLOSED[i] = VoxelShapeUtil.rotate(INTERACTION_SHAPE_CLOSED, i);
			INTERACTION_SHAPES_OPEN[i] = VoxelShapeUtil.rotate(INTERACTION_SHAPE_OPEN, i);
			COLLISION_SHAPES_CLOSED[i] = VoxelShapeUtil.rotate(COLLISION_SHAPE_CLOSED, i);
			COLLISION_SHAPES_OPEN[i] = VoxelShapeUtil.rotate(COLLISION_SHAPE_OPEN, i);
		}
	}

	public BlockTatooineHomeDoorController(Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getShape(state, world, pos, INTERACTION_SHAPES_OPEN, INTERACTION_SHAPES_CLOSED);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getShape(state, world, pos, COLLISION_SHAPES_OPEN, COLLISION_SHAPES_CLOSED);
	}

	@Override
	protected BlockPos getController(BlockView world, BlockPos self)
	{
		return self;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new TatooineHomeDoorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != SwgBlocks.Door.TatooineHomeBlockEntityType)
			return null;
		return TatooineHomeDoorBlockEntity::tick;
	}
}
