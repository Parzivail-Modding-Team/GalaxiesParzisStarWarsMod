package com.parzivail.pswg.mixin;

import com.mojang.serialization.Lifecycle;
import com.parzivail.pswg.util.Lumberjack;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelProperties.class)
public class LevelPropertiesMixin
{
	@Shadow
	@Final
	private Lifecycle lifecycle;

	@Inject(method = "getLifecycle()Lcom/mojang/serialization/Lifecycle;", at = @At("HEAD"), cancellable = true)
	private void getLifecycle(CallbackInfoReturnable<Lifecycle> cir)
	{
		if (lifecycle == Lifecycle.experimental())
		{
			Lumberjack.warn("Supressing EXPERIMENTAL level lifecycle");
			cir.setReturnValue(Lifecycle.stable());
			cir.cancel();
		}
	}
}
