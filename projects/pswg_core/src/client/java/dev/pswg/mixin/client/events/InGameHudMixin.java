package dev.pswg.mixin.client.events;

import dev.pswg.events.HudRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin
{
	@Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
	void renderCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci)
	{
		var matrix = context.pose();

		matrix.pushMatrix();
		HudRenderEvents.CROSSHAIR.invoker().render(context, tickCounter);
		matrix.popMatrix();
	}
}
