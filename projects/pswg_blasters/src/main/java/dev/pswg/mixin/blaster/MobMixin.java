package dev.pswg.mixin.blaster;

import dev.pswg.entity.BlasterStatusEffects;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pauses mob goals while a temporary blaster disable effect is active.
 */
@Mixin(Mob.class)
public abstract class MobMixin
{
	/**
	 * Leaves effect ticking and the permanent NoAI flag unchanged.
	 */
	@Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
	private void pswg$pauseDisabledGoals(CallbackInfo callback)
	{
		var mob = (Mob)(Object)this;
		if (BlasterStatusEffects.disabled(mob))
		{
			mob.getNavigation().stop();
			mob.setTarget(null);
			callback.cancel();
		}
	}
}
