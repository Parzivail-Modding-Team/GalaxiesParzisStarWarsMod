package dev.pswg.mixin.client;

import dev.pswg.Blasters;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin
{
	/**
	 * Draw the current heat dissipation in blaster stack overlays
	 */
	@Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCooldownProgress(Lnet/minecraft/item/ItemStack;II)V", shift = At.Shift.AFTER))
	public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci)
	{
		var self = (DrawContext)(Object)this;
		var client = MinecraftClient.getInstance();

		if (stack.isOf(Blasters.BLASTER))
		{
			// TODO: implement cooldown heat bar
			// Drawables.itemDurability(self, value, x, y - 13, 13, 0x0000FF);
		}
	}
}
