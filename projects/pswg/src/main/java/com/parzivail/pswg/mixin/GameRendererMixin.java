package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.camera.CameraHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin
{
	@Shadow
	@Final
	private Camera camera;

	@Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;Lorg/joml/Matrix4f;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	void applyCameraTransformations$setupFrustum(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci, boolean bl, Camera camera, MatrixStack matrixStack, double d, float f, float g, Matrix4f matrix4f, Matrix3f matrix3f)
	{
		CameraHelper.applyCameraTransformations(tickDelta, limitTime, matrices, camera);
	}

	@Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiplyPositionMatrix(Lorg/joml/Matrix4f;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	void applyCameraTransformations$multiply(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci, boolean bl, Camera camera, MatrixStack matrixStack, double fov)
	{
		CameraHelper.applyCameraShake(tickDelta, limitTime, matrices, camera, fov);
	}

	@Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "HEAD"))
	void renderWorldHead(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci)
	{
		CameraHelper.renderWorldHead(tickDelta, limitTime, matrix, camera);
	}

	@Inject(method = "renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At("HEAD"), cancellable = true)
	void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
	{
		CameraHelper.renderHand(matrices, camera, tickDelta, ci);
	}
}
