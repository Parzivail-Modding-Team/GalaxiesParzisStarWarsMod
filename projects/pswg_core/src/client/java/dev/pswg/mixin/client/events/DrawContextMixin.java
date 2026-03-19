package dev.pswg.mixin.client.events;

import dev.pswg.events.ItemRenderEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class DrawContextMixin
{
	@Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemCooldown(Lnet/minecraft/world/item/ItemStack;II)V", shift = At.Shift.AFTER))
	public void drawStackOverlay(Font textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci)
	{
		var self = (GuiGraphics)(Object)this;

		var matrix = self.pose();

		matrix.pushMatrix();
		ItemRenderEvents.STACK.invoker().render(self, textRenderer, stack, x, y);
		matrix.popMatrix();
	}
}
