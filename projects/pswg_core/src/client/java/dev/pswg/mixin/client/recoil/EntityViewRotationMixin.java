package dev.pswg.mixin.client.recoil;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.pswg.interaction.IRecoilEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Applies the local player's recoil pitch to the interpolated view rotation.
 */
@Mixin(Entity.class)
public abstract class EntityViewRotationMixin
{
	/**
	 * Adds recoil pitch to the local player's interpolated view rotation.
	 *
	 * @param original vanilla view pitch
	 * @param tickDelta partial tick progress
	 * @return the recoil-adjusted view pitch
	 */
	@ModifyReturnValue(method = "getViewXRot(F)F", at = @At("RETURN"))
	private float pswg$applyRecoilPitch(float original, float tickDelta)
	{
		if (!((Object)this instanceof LocalPlayer player) || !(player instanceof IRecoilEntity recoilEntity))
			return original;

		return Math.clamp(original + tickDelta * recoilEntity.pswg$getRecoilVelocity().x, -90, 90);
	}
}
