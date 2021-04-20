package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.PProjectileEntityDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@Shadow
	protected float lastDamageTaken;

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"))
	public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if (source instanceof PProjectileEntityDamageSource && ((PProjectileEntityDamageSource)source).ignoresInvulnerableFrames())
			lastDamageTaken = 0;
	}
}
