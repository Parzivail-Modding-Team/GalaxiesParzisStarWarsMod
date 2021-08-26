package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.IFlyingVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Shadow @Nullable public abstract Entity getVehicle();

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void noShipFallDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (damageSource == DamageSource.FALL && getVehicle() instanceof IFlyingVehicle) {
			cir.setReturnValue(true);
		}
	}
}
