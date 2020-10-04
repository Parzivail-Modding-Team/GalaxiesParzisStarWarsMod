package com.parzivail.pswg.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
	@Redirect(method = "Lnet/minecraft/client/render/WorldRenderer;reload()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isFabulousGraphicsOrBetter()Z"))
	private boolean reload()
	{
		return true;
	}
}
