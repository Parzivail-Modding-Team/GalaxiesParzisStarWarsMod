package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.sound.LightsaberIdleSoundInstance;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.world.InventoryUtil;
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

@Mixin(PlayerEntity.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityMixin
{
	@Unique
	private ItemStack lastSelectedItemRef;
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

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE))
	private void onTick(CallbackInfo ci)
	{
		var self = (PlayerEntity)(Object)this;
		var inv = self.getInventory();

		var resultSlot = InventoryUtil.getSlotWithStack(inv, lastSelectedItemRef);

		if (resultSlot == -1 || resultSlot == inv.selectedSlot)
		{
			lastSelectedItemRef = self.getMainHandStack();
			return;
		}

		var stack = lastSelectedItemRef;

		if (stack.getItem() instanceof BlasterItem)
		{
			if (stack.hasNbt())
			{
				BlasterTag.mutate(stack, tag -> tag.isAimingDownSights = false);
			}
		}
		else if (stack.getItem() instanceof LightsaberItem)
		{
			// TODO: play sound
			if (stack.hasNbt())
				LightsaberTag.mutate(stack, tag -> {
					tag.active = false;
					tag.finalizeMovement();
				});
		}

		self.getInventory().setStack(resultSlot, stack);
		lastSelectedItemRef = self.getMainHandStack();
	}
}
