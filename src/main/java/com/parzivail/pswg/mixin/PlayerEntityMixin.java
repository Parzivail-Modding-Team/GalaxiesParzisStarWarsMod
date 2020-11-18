package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.sound.LightsaberIdleSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityMixin
{
	@Unique
	private boolean metConditionsForLightsaberSound = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci)
	{
		PlayerEntity player = (PlayerEntity)(Object)this;
		if (!player.world.isClient)
			return;

		boolean meetsConditionsForLightsaberSound = LightsaberIdleSoundInstance.areConditionsMet(player);
		if (meetsConditionsForLightsaberSound && !metConditionsForLightsaberSound)
		{
			Client.minecraft.getSoundManager().play(new LightsaberIdleSoundInstance(player));
		}
		metConditionsForLightsaberSound = meetsConditionsForLightsaberSound;
	}
}
