package dev.pswg.mixin.events;

import dev.pswg.item.IItemAddedToInventoryListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin
{
	@Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
	public void add(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir){
		if(itemStack.getItem() instanceof IItemAddedToInventoryListener listener)
			listener.onAddedToInventory(itemStack);
	}
}
