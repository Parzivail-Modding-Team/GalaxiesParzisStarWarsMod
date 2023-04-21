package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgTags;
import com.parzivail.pswg.features.blasters.BlasterItem;
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

	@Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"))
	public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if (source.isIn(SwgTags.DamageTypes.IS_IGNORES_INVULNERABLE_FRAMES))
			lastDamageTaken = 0;
	}

	@Inject(method = "isClimbing()Z", at = @At("HEAD"), cancellable = true)
	public void isClimbing(CallbackInfoReturnable<Boolean> cir)
	{
		if (BlasterItem.areBothHandsOccupied((LivingEntity)(Object)this))
			cir.setReturnValue(false);
	}
}
