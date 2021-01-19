package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CactusBlock.class)
public abstract class CactusMixin
{
	@Inject(method = "canPlaceAt", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void canPlaceOnTatooineSand(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
	{
		BlockState blockState2 = world.getBlockState(pos.down());
		if (blockState2.isOf(SwgBlocks.Sand.Tatooine) || blockState2.isOf(SwgBlocks.Sand.DenseTatooine))
			cir.setReturnValue(true);
	}

}
