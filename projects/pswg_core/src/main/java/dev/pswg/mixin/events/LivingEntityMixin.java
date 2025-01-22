package dev.pswg.mixin.events;

import dev.pswg.Galaxies;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Shadow
	protected float lastDamageTaken;

	@Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"))
	public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if (source.isIn(Galaxies.IGNORES_INVULNERABLE_FRAMES))
		{
			lastDamageTaken = 0;
		}
	}
}
