package com.parzivail.pswg.client.render.camera;

import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.features.blasters.client.BlasterRecoilManager;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CameraHelper
{
	public static final MutableCameraEntity MUTABLE_CAMERA_ENTITY = new MutableCameraEntity();

	public static boolean forcePlayerRender = false;

	public static void applyCameraTransformations(float tickDelta, long limitTime, MatrixStack matrix, Camera camera)
	{
		var minecraft = MinecraftClient.getInstance();
		assert minecraft.player != null;

		var ship = ShipEntity.getShip(minecraft.player);

		if (ship == null)
			return;

		if (ship.usePlayerPerspective())
			return;

		// Undo what is expected to be the player's current rotation transformation
		matrix.multiply(new Quaternionf().rotationY(MathUtil.toRadians(-(camera.getYaw() + 180.0F))));
		matrix.multiply(new Quaternionf().rotationX(MathUtil.toRadians(-camera.getPitch())));

		var r = new Quaternionf(ship.getViewRotation(tickDelta));

		if (minecraft.options.getPerspective() == Perspective.THIRD_PERSON_FRONT)
			r.mul(QuatUtil.ROT_Y_180);

		r.conjugate();
		matrix.multiply(r);

		if (minecraft.options.getPerspective().isFirstPerson())
			matrix.translate(0, -0.1, 0);
	}

	public static void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
	{
		var minecraft = MinecraftClient.getInstance();

		if (minecraft.getCameraEntity() instanceof MutableCameraEntity || minecraft.getCameraEntity() instanceof ShipEntity)
			ci.cancel();
	}

	public static void renderWorldHead(float tickDelta, long limitTime, MatrixStack matrix, Camera camera)
	{
		var minecraft = MinecraftClient.getInstance();
		var player = minecraft.player;

		assert player != null;

		var ship = ShipEntity.getShip(player);

		if (ship != null)
		{
			if (ship.usePlayerPerspective())
			{
				minecraft.setCameraEntity(player);
				//				QuatUtil.updateEulerRotation(minecraft.cameraEntity, ship.getViewRotation(minecraft.getTickDelta()));
			}
			else if (minecraft.options.getPerspective() != Perspective.FIRST_PERSON)
				minecraft.setCameraEntity(CameraHelper.MUTABLE_CAMERA_ENTITY.with(ship, ship.getCamera()));
			else
				minecraft.setCameraEntity(ship);

			return;
		}

		if ((minecraft.getCameraEntity() instanceof MutableCameraEntity mce && !mce.shouldForce()) || minecraft.getCameraEntity() instanceof ShipEntity)
			minecraft.setCameraEntity(player);
	}

	public static void playerRenderHead(AbstractClientPlayerEntity player, MatrixStack matrixStack, CallbackInfo ci)
	{
		if (forcePlayerRender)
			return;

		var ship = ShipEntity.getShip(player);
		if (ship != null && !ship.usePlayerPerspective())
		{
			ci.cancel();
			return;
		}

		var pc = SwgEntityComponents.getPersistent(player);
		var species = pc.getSpecies();
		if (species == null)
			return;

		var f = species.getScaleFactor();

		matrixStack.push();
		matrixStack.scale(f, f, f);
	}

	public static void playerRenderReturn(AbstractClientPlayerEntity player, MatrixStack matrixStack, CallbackInfo ci)
	{
		var pc = SwgEntityComponents.getPersistent(player);
		var species = pc.getSpecies();
		if (species == null)
			return;

		matrixStack.pop();
	}

	public static void applyCameraShake(float tickDelta, long limitTime, MatrixStack matrix, Camera camera, double fov)
	{
		var minecraft = MinecraftClient.getInstance();

		BlasterRecoilManager.applyCameraShake(minecraft, matrix, camera, tickDelta, fov);
	}
}
