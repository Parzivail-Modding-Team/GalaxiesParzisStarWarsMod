package dev.pswg.mixin.client.attributes;

import dev.pswg.GalaxiesClient;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.interaction.IRecoilEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin
{
	/**
	 * Append our field of view changes to the client player's field of view calculations
	 *
	 * @param fieldOfView The client's current field of view
	 *
	 * @return The modified value of the client's field of view
	 */
	@ModifyArg(method = "getFieldOfViewModifier(ZF)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"), index = 2)
	public float getFovMultiplier(float fieldOfView)
	{
		var self = (Player)(Object)this;

		fieldOfView /= (float)self.getAttributeValue(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM);

		if (self instanceof IRecoilEntity recoilEntity)
		{
			var recoilTime = recoilEntity.pswg$getRecoilTime();
			if (recoilTime > 0)
			{
				fieldOfView /= recoilEntity.pswg$getRecoilFovMultiplier(self, GalaxiesClient.getTickDelta());
			}
		}

		return fieldOfView;
	}
}
