package com.parzivail.pswg.mixin;

import com.parzivail.util.client.render.ICustomSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin
{
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private VertexBuffer lightSkyBuffer;

	@Shadow
	private VertexBuffer darkSkyBuffer;

	@Shadow
	private VertexBuffer starsBuffer;

	@Inject(method = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER), cancellable = true)
	private void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Runnable fogApplier, CallbackInfo ci)
	{
		var renderer = ICustomSkyRenderer.CUSTOM_SKY_RENDERERS.get(client.world.getRegistryKey().getValue());
		if (renderer != null)
		{
			renderer.render(client, lightSkyBuffer, darkSkyBuffer, starsBuffer, matrices, projectionMatrix, tickDelta, fogApplier, ci);
			ci.cancel();
		}
	}
}
