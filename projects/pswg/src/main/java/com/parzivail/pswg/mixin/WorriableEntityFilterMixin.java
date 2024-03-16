package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoxEntity.WorriableEntityFilter.class)
public class WorriableEntityFilterMixin
{

	@Inject(method = "test(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
	private void pswm_test(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if (livingEntity instanceof SandSkitterEntity) {
			cir.setReturnValue(false);
		}
	}

}
