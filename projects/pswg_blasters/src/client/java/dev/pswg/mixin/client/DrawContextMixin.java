package dev.pswg.mixin.client;

import dev.pswg.Blasters;
import dev.pswg.GalaxiesClient;
import dev.pswg.item.BlasterItem;
import dev.pswg.rendering.Drawables;
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
	 * Draw the current lastTotalHeat dissipation in blaster stack overlays
	 */
	@Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCooldownProgress(Lnet/minecraft/item/ItemStack;II)V", shift = At.Shift.AFTER))
	public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci)
	{
		var self = (DrawContext)(Object)this;
		var client = MinecraftClient.getInstance();

		if (stack.isOf(Blasters.BLASTER_ITEM))
		{
			assert client.world != null;

			// TODO: better visual
			BlasterItem.getFireCooldownProgress(client.world, stack, GalaxiesClient.getTickDelta())
			           .ifPresent(value -> Drawables.itemDurability(self, value, x, y - 13, 13, 0x0000FF));

			var heat = BlasterItem.getHeat(client.world, stack, GalaxiesClient.getTickDelta());
			Drawables.itemDurability(self, heat / 10, x, y - 10, 13, 0x0000FF);
		}
	}
}
