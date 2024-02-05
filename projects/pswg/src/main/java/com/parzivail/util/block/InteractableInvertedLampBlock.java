package com.parzivail.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InteractableInvertedLampBlock extends InvertedLampBlock
{
	public InteractableInvertedLampBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!player.getStackInHand(hand).isEmpty())
			return super.onUse(state, world, pos, player, hand, hit);

		if (!player.getAbilities().allowModifyWorld)
			return ActionResult.PASS;
		else
		{
			updateState(state.cycle(INVERTED), world, pos);
			return ActionResult.success(world.isClient);
		}
	}
}
