package dev.pswg.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InteractableInvertedLampSlab extends InvertedLampSlab
{
	public InteractableInvertedLampSlab(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		if (!player.getMainHandStack().isEmpty())
			return super.onUse(state, world, pos, player, hit);

		if (!player.getAbilities().allowModifyWorld)
			return ActionResult.PASS;
		else
		{
			updateState(state.cycle(INVERTED), world, pos);
			return ActionResult.SUCCESS;
		}
	}
}