package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Carver.class)
public class CarverMixin
{
	@Inject(method = "canAlwaysCarveBlock(Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
	private void canAlwaysCarveBlock(BlockState state, CallbackInfoReturnable<Boolean> cir)
	{
		if (state.getBlock() == SwgBlocks.Sand.Desert)
		{
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
}
