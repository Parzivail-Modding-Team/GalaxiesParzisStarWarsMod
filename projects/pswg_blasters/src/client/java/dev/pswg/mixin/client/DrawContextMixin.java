package dev.pswg.mixin.client;

import dev.pswg.Blasters;
import dev.pswg.GalaxiesClient;
import dev.pswg.item.BlasterItem;
import dev.pswg.rendering.Drawables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
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
			var nextTimestamp = stack.get(BlasterItem.NEXT_TIMESTAMP_COMPONENT);
			if (nextTimestamp == null)
				return;

			var currentTimestamp = client.world.getTime() + GalaxiesClient.getTickDelta();

			var value = MathHelper.clamp((nextTimestamp - currentTimestamp) / 10f, 0, 1);

			Drawables.itemDurability(self, value, x, y - 13, 13, 0x0000FF);
		}
	}
}
