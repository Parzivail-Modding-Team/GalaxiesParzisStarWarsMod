package com.parzivail.pswg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.parzivail.util.item.ICooldownItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin
{
	@Shadow @Final private MinecraftClient client;

	@ModifyExpressionValue(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;getCooldownProgress(Lnet/minecraft/item/Item;F)F"))
	private float modifyCooldown(float originalCooldown, TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride)
	{
		if (stack.getItem() instanceof ICooldownItem item)
		{
			return item.getCooldownProgress(this.client.player, stack, this.client.getRenderTickCounter().getTickDelta(true));
		}
		return originalCooldown;
	}
}
