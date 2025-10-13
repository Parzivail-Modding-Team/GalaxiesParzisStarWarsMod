package dev.pswg.mixin.client.recoil;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.pswg.GalaxiesClient;
import dev.pswg.interaction.RecoilEntityAttachment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Main featureset for recoil support in client players
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin
{
	@Unique
	private Vector3f pswg$currentRecoilVelocity = new Vector3f();

	@Inject(method = "tickMovement()V", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci)
	{
		var self = (ClientPlayerEntity)(Object)this;

		var recoil = RecoilEntityAttachment.get(self);

		pswg$currentRecoilVelocity = recoil.recoilVelocity();
	}

	@ModifyReturnValue(method = "getPitch(F)F", at = @At("RETURN"))
	private float getPitch(float original, float tickDelta)
	{
		return Math.clamp(original + tickDelta * pswg$currentRecoilVelocity.x, -90, 90);
	}

	@ModifyReturnValue(method = "getYaw(F)F", at = @At("RETURN"))
	private float getYaw(float original, float tickDelta)
	{
		// Lerp "into" the current value
		return original - (1 - tickDelta) * pswg$currentRecoilVelocity.y;
	}
}
