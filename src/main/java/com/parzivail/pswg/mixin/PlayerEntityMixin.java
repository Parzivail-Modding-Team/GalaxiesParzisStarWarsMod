package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.sound.LightsaberIdleSoundInstance;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityMixin
{
	@Unique
	private boolean metConditionsForLightsaberSound = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci)
	{
		var player = (PlayerEntity)(Object)this;
		if (!player.world.isClient)
			return;

		var meetsConditionsForLightsaberSound = LightsaberIdleSoundInstance.areConditionsMet(player);
		if (meetsConditionsForLightsaberSound && !metConditionsForLightsaberSound)
		{
			var minecraft = MinecraftClient.getInstance();
			minecraft.getSoundManager().play(new LightsaberIdleSoundInstance(player));
		}
		metConditionsForLightsaberSound = meetsConditionsForLightsaberSound;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onSwitchItem(CallbackInfo ci, int i, double d, ItemStack itemStack)
	{
		if (itemStack.getItem() == SwgItems.Blaster.Blaster) {
			if (itemStack.hasTag()) {
				BlasterTag.mutate(itemStack, tag -> tag.isAimingDownSights = false);
			}
		}
	}
}
