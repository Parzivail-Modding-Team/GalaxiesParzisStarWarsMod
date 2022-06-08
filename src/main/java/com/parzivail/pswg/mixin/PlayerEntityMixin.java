package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.world.InventoryUtil;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
	@Unique
	private ItemStack lastSelectedItemRef;

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
					if (!self.world.isClient && tag.transition == 0)
						LightsaberItem.playSound(self.world, self, tag);
					tag.finalizeMovement();
				});
		}

		self.getInventory().setStack(resultSlot, stack);
		lastSelectedItemRef = self.getMainHandStack();
	}

	@Inject(method = "isPartVisible(Lnet/minecraft/client/render/entity/PlayerModelPart;)Z", at = @At("HEAD"), cancellable = true)
	public void a(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir)
	{
		var self = (PlayerEntity)(Object)this;

		if (modelPart == PlayerModelPart.HAT && ArmorRenderer.getModArmor(self, EquipmentSlot.HEAD) != null)
			cir.setReturnValue(false);
		else if ((modelPart == PlayerModelPart.LEFT_SLEEVE || modelPart == PlayerModelPart.RIGHT_SLEEVE) && ArmorRenderer.getModArmor(self, EquipmentSlot.CHEST) != null)
			cir.setReturnValue(false);
		else if ((modelPart == PlayerModelPart.LEFT_PANTS_LEG || modelPart == PlayerModelPart.RIGHT_PANTS_LEG) && ArmorRenderer.getModArmor(self, EquipmentSlot.LEGS) != null)
			cir.setReturnValue(false);
	}
}
