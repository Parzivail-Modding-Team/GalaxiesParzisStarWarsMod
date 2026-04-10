package dev.pswg.mixin.client.events;

import dev.pswg.events.HudRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin
{
	@Inject(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0))
	void renderCrosshair(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci)
	{
		var matrix = context.pose();

		matrix.pushMatrix();
		HudRenderEvents.CROSSHAIR.invoker().render(context, tickCounter);
		matrix.popMatrix();
	}
}
