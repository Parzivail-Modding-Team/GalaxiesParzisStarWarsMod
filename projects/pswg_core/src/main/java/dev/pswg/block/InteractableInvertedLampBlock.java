package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class InteractableInvertedLampBlock extends InvertedLampBlock
{
	public InteractableInvertedLampBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
	{
		if (!player.getMainHandItem().isEmpty())
			return super.useWithoutItem(state, world, pos, player, hit);

		if (!player.getAbilities().mayBuild)
			return InteractionResult.PASS;
		else
		{
			updateState(state.cycle(INVERTED), world, pos);
			return InteractionResult.SUCCESS;
		}
	}
}
