package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import com.parzivail.util.client.render.ICustomSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
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
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Shadow
	private VertexBuffer lightSkyBuffer;

	@Shadow
	private VertexBuffer darkSkyBuffer;

	@Shadow
	private VertexBuffer starsBuffer;

	@Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER), cancellable = true)
	private void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable fogApplier, CallbackInfo ci)
	{
		var renderer = ICustomSkyRenderer.REGISTRY.get(client.world.getRegistryKey().getValue());
		if (renderer != null)
		{
			renderer.render(client, lightSkyBuffer, darkSkyBuffer, starsBuffer, matrices, projectionMatrix, tickDelta, fogApplier, ci);
			ci.cancel();
		}
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 4, shift = At.Shift.BEFORE))
	private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci)
	{
		EnergyRenderer.renderLayer(bufferBuilders);
	}
}
