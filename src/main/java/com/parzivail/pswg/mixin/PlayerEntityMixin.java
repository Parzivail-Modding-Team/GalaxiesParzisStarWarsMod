package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.sound.LightsaberIdleSoundInstance;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityMixin
{
	@Shadow
	@Final
	private PlayerInventory inventory;
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
		if (itemStack.getItem() instanceof BlasterItem)
		{
			if (itemStack.hasTag())
				BlasterTag.mutate(itemStack, tag -> tag.isAimingDownSights = false);
		}
		else if (itemStack.getItem() instanceof LightsaberItem)
		{
			if (itemStack.hasTag())
				LightsaberTag.mutate(itemStack, tag -> {
					tag.active = false;
					tag.finalizeMovement();
				});
		}
	}
}
