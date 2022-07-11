package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.PowerCouplingBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithBounds;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PowerCouplingBlock extends WaterloggableRotatingBlockWithBounds implements BlockEntityProvider
{
	private static final VoxelShape SHAPE_SINGLE = VoxelShapes.cuboid(0, 5.5f / 16, 5.5f / 16, 4 / 16f, 10.5f / 16, 10.5f / 16);

	public PowerCouplingBlock(Substrate requiresSubstrate, Settings settings)
	{
		super(VoxelShapes.empty(), requiresSubstrate, settings);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return super.getPlacementStateBlockBased(ctx);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new PowerCouplingBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != SwgBlocks.Power.CouplingBlockEntityType)
			return null;
		return world.isClient ? null : PowerCouplingBlockEntity::serverTick;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapeUtil.rotateToFace(SHAPE_SINGLE, state.get(FACING));
	}
}
