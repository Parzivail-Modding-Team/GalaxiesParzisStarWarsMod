package dev.pswg.mixin.events;

import dev.pswg.Galaxies;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Shadow
	protected float lastHurt;

	@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"))
	public void damage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if (source.is(Galaxies.IGNORES_INVULNERABLE_FRAMES))
		{
			lastHurt = 0;
		}
	}
}
