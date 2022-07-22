package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.util.item.IItemHotbarListener;
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
public abstract class PlayerEntityMixin
{
	@Unique
	private ItemStack previousStackRef;

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE))
	private void onTick(CallbackInfo ci)
	{
		var self = (PlayerEntity)(Object)this;
		var inv = self.getInventory();

		var previousStackSlot = InventoryUtil.getSlotWithStack(inv, previousStackRef);

		if (previousStackSlot == -1 || previousStackSlot == inv.selectedSlot)
		{
			previousStackRef = self.getMainHandStack();
			return;
		}

		var prevStack = previousStackRef;
		var currentStack = self.getMainHandStack();

		if (currentStack.getItem() instanceof IItemHotbarListener listener && listener.onItemSelected(self, currentStack))
			self.getInventory().setStack(inv.selectedSlot, currentStack);

		if (prevStack.getItem() instanceof IItemHotbarListener listener && listener.onItemDeselected(self, prevStack))
			self.getInventory().setStack(previousStackSlot, prevStack);

		previousStackRef = self.getMainHandStack();
	}

	@Inject(method = "isPartVisible(Lnet/minecraft/client/render/entity/PlayerModelPart;)Z", at = @At("HEAD"), cancellable = true)
	public void isPartVisible(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir)
	{
		var self = (PlayerEntity)(Object)this;

		if (modelPart == PlayerModelPart.HAT && ArmorRenderer.getModArmor(self, EquipmentSlot.HEAD) != null)
			cir.setReturnValue(false);
		else if ((modelPart == PlayerModelPart.LEFT_SLEEVE || modelPart == PlayerModelPart.RIGHT_SLEEVE) && ArmorRenderer.getModArmor(self, EquipmentSlot.CHEST) != null)
			cir.setReturnValue(false);
		else if ((modelPart == PlayerModelPart.LEFT_PANTS_LEG || modelPart == PlayerModelPart.RIGHT_PANTS_LEG) && ArmorRenderer.getModArmor(self, EquipmentSlot.LEGS) != null)
			cir.setReturnValue(false);
	}

	// TODO: collisions sometimes break
//	@Inject(method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("RETURN"), cancellable = true)
//	public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir)
//	{
//		var self = (PlayerEntity)(Object)this;
//
//		var pc = SwgEntityComponents.getPersistent(self);
//
//		var species = pc.getSpecies();
//		if (species == null)
//			return;
//
//		var f = species.getScaleFactor();
//		if (f == 1)
//			return;
//
//		var value = cir.getReturnValue();
//		cir.setReturnValue(value.scaled(f));
//	}
//
//	@Inject(method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F", at = @At("RETURN"), cancellable = true)
//	public void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir)
//	{
//		var self = (PlayerEntity)(Object)this;
//		if (self.age == 0)
//			return;
//
//		var pc = SwgEntityComponents.getPersistent(self);
//
//		var species = pc.getSpecies();
//		if (species == null)
//			return;
//
//		var f = species.getScaleFactor();
//		if (f == 1)
//			return;
//
//		var value = cir.getReturnValue();
//		cir.setReturnValue(value * f);
//	}
}
