package com.parzivail.pswg.client.camera;

import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.util.QuatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;

public class CameraHelper
{
	public static void applyCameraTransformations(float tickDelta, long limitTime, MatrixStack matrix, Camera camera)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		Entity cameraEntity = mc.cameraEntity;

		assert cameraEntity != null;

		if (cameraEntity instanceof ShipEntity)
		{
			ShipEntity ship = (ShipEntity)cameraEntity;

			EulerAngle angle = QuatUtil.toEulerAngles(ship.getRotation(tickDelta));

			matrix.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(angle.getPitch()));
			matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(angle.getYaw() + (float)Math.PI));

			float f = (float)Math.cos(-angle.getYaw() - Math.PI);
			float g = (float)Math.sin(-angle.getYaw() - Math.PI);
			float h = -MathHelper.cos(-angle.getPitch());
			float i = MathHelper.sin(-angle.getPitch());
			matrix.multiply(new Vector3f(g * h, i, f * h).getRadialQuaternion(angle.getRoll()));
		}
		else
		{
			matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
			matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
		}
	}
}
