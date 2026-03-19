package dev.pswg.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class WaterloggableRotatingBlock extends WaterloggableBlock
{
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

	public WaterloggableRotatingBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	public BlockState getPlacementStateBlockBased(BlockPlaceContext ctx)
	{
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getClickedFace());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}
}
