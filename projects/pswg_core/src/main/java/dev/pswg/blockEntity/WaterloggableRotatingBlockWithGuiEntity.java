package dev.pswg.blockEntity;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WaterloggableRotatingBlockWithGuiEntity extends WaterloggableRotatingBlockWithEntity
{
	private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityBiFunction;

	public WaterloggableRotatingBlockWithGuiEntity(Properties settings, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityBiFunction)
	{
		super(settings);
		this.blockEntityBiFunction = blockEntityBiFunction;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		if (!getBlockEntityUsePos(state, pos).equals(pos))
			return null;
		return blockEntityBiFunction.apply(pos, state);
	}

	protected BlockPos getBlockEntityUsePos(BlockState state, BlockPos pos)
	{
		return pos;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
	{
		if (world.isClientSide())
			return InteractionResult.SUCCESS;
		else
		{
			player.openMenu(state.getMenuProvider(world, getBlockEntityUsePos(state, pos)));
			return InteractionResult.CONSUME;
		}
	}
}