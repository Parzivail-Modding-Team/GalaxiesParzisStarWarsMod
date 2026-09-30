package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class InteractableConnectingInvertedLampBlock extends SelfConnectingBlock
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

	public static boolean isLit(BlockState state)
	{
		return state.getValue(POWERED) ^ state.getValue(INVERTED);
	}

	public InteractableConnectingInvertedLampBlock(BlockBehaviour.Properties settings)
	{
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(INVERTED, true).setValue(LIT, true));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!player.getAbilities().mayBuild)
			return InteractionResult.PASS;
		else
		{
			updateState(state.cycle(INVERTED), level, pos);
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(POWERED, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
		if (!level.isClientSide())
			updateState(state.setValue(POWERED, level.hasNeighborSignal(pos)), level, pos);

		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED) && !level.hasNeighborSignal(pos))
			updateState(state.cycle(POWERED), level, pos);
		super.tick(state, level, pos, random);
	}

	protected static boolean updateState(BlockState state, Level world, BlockPos pos)
	{
		state = state.setValue(LIT, isLit(state));
		return world.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT, POWERED, INVERTED);
		super.createBlockStateDefinition(builder);
	}
}