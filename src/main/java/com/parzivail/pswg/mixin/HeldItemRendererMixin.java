package com.parzivail.pswg.mixin;

import com.parzivail.util.client.render.ICustomVisualItemEquality;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
@Environment(EnvType.CLIENT)
public class HeldItemRendererMixin
{
	@Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
	private boolean areStacksEqual(ItemStack original, ItemStack updated)
	{
		if (updated.getItem().equals(original.getItem()) && updated.getItem() instanceof ICustomVisualItemEquality)
			return ((ICustomVisualItemEquality)updated.getItem()).areStacksVisuallyEqual(original, updated);

		return ItemStack.areEqual(original, updated);
	}
}
