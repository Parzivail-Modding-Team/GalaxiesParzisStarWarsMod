package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.camera.CameraHelper;
import com.parzivail.util.item.IZoomingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin
{
	@Final
	@Shadow
	private MinecraftClient client;

	@Unique
	private double formerFov;

	@Inject(at = @At("TAIL"), method = "Lnet/minecraft/client/render/GameRenderer;loadShaders(Lnet/minecraft/resource/ResourceManager;)V")
	private void reloadShaders(ResourceManager manager, CallbackInfo ci) throws IOException
	{
		Client.lightsaberShader = new Shader(manager, "rendertype_lightsaber", VertexFormats.POSITION_COLOR);
	}

	@Inject(at = @At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
	private void getZoomedFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir)
	{
		var zoomedFov = cir.getReturnValue();

		if (client.cameraEntity instanceof PlayerEntity player)
		{
			var stack = player.getStackInHand(Hand.MAIN_HAND);
			if (stack.getItem() instanceof IZoomingItem)
			{
				zoomedFov *= ((IZoomingItem)stack.getItem()).getFovMultiplier(stack, client.world, player);
			}
		}

		if (zoomedFov > formerFov)
		{
			if (changingFov)
			{
				this.client.worldRenderer.scheduleTerrainUpdate();
			}
		}

		this.formerFov = zoomedFov;

		cir.setReturnValue(zoomedFov);
	}

	@Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Matrix4f;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	void applyCameraTransformations(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci, boolean shouldRenderBlockOutline, Camera camera)
	{
		CameraHelper.applyCameraTransformations(tickDelta, limitTime, matrix, camera);
	}

	@Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "HEAD"))
	void renderWorldHead(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci)
	{
		CameraHelper.renderWorldHead(tickDelta, limitTime, matrix);
	}

	@Inject(method = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At("HEAD"), cancellable = true)
	void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
	{
		CameraHelper.renderHand(matrices, camera, tickDelta, ci);
	}
}
