package com.parzivail.pswg.mixin;

import com.parzivail.pswg.item.LightsaberItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
	@Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
	private boolean areStacksEqual(ItemStack original, ItemStack updated)
	{
		if (updated.getItem().equals(original.getItem()) && updated.getItem() instanceof LightsaberItem)
			return true;

		return ItemStack.areEqual(original, updated);
	}
}
