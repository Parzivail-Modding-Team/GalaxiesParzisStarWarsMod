package dev.pswg.mixin.client;

import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin
{
	/**
	 * Append our field of view changes to the client player's field of view calculations
	 *
	 * @param fieldOfView The client's current field of view
	 *
	 * @return The modified value of the client's field of view
	 */
	@ModifyArg(method = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getFovMultiplier(ZF)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"), index = 2)
	public float getFovMultiplier(float fieldOfView)
	{
		var self = (PlayerEntity)(Object)this;
		return fieldOfView / (float)self.getAttributeValue(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM);
	}
}
