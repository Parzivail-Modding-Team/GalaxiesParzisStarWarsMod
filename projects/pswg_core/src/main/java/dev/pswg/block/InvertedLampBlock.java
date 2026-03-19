package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public class InvertedLampBlock extends Block
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

	public static boolean isLit(BlockState state)
	{
		return state.getValue(POWERED) ^ state.getValue(INVERTED);
	}

	public InvertedLampBlock(BlockBehaviour.Properties settings)
	{
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(INVERTED, true).setValue(LIT, true));
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return this.defaultBlockState().setValue(POWERED, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify)
	{
		if (!world.isClientSide())
			updateState(state.setValue(POWERED, world.hasNeighborSignal(pos)), world, pos);
		super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		if (state.getValue(POWERED) && !world.hasNeighborSignal(pos))
			updateState(state.cycle(POWERED), world, pos);
	}

	protected static boolean updateState(BlockState state, Level world, BlockPos pos)
	{
		state = state.setValue(LIT, isLit(state));
		return world.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(LIT, POWERED, INVERTED);
	}
}
