package com.parzivail.pswg.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin
{
	// TODO: is there a cleaner way to prevent blaster items from de-syncing when dropped?
	@Redirect(method = "method_37412(Lnet/minecraft/entity/player/PlayerInventory;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setPreviousTrackedSlot(ILnet/minecraft/item/ItemStack;)V"))
	private void pswg_disableDropTrackedStackOverride(ScreenHandler instance, int slot, ItemStack stack) {}
}
