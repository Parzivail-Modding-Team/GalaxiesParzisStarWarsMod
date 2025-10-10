package dev.pswg.mixin.client.events;

import dev.pswg.events.HudRenderEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
	@Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
	void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
	{
		var matrix = context.getMatrices();

		matrix.pushMatrix();
		HudRenderEvents.CROSSHAIR.invoker().render(context, tickCounter);
		matrix.popMatrix();
	}
}
