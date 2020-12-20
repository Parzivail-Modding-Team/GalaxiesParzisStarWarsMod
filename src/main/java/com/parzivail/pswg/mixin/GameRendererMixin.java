package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.camera.CameraHelper;
import com.parzivail.pswg.item.IZoomingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin
{
	@Final
	@Shadow
	private MinecraftClient client;

	@Unique
	private double formerFov;

	@Inject(at = @At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
	private void getZoomedFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir)
	{
		double fov = cir.getReturnValue();
		double zoomedFov = fov;

		if (client.cameraEntity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)client.cameraEntity;
			ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
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

	@Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	void applyCameraTransformations(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info, boolean shouldRenderBlockOutline, Camera camera)
	{
		CameraHelper.applyCameraTransformations(tickDelta, limitTime, matrix, camera);
	}

	@Inject(method = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At("HEAD"), cancellable = true)
	void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
	{
		CameraHelper.renderHand(matrices, camera, tickDelta, ci);
	}

	@Redirect(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V", ordinal = 2))
	void noopCameraYaw(MatrixStack stack, Quaternion q)
	{
	}

	@Redirect(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V", ordinal = 3))
	void noopCameraPitch(MatrixStack stack, Quaternion q)
	{
	}
}
