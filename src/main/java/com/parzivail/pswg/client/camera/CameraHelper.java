package com.parzivail.pswg.client.camera;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.entity.ShipEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;

public class CameraHelper
{
	public static void applyCameraTransformations(float tickDelta, long limitTime, MatrixStack matrix, Camera camera)
	{
		assert Client.minecraft.player != null;

		ShipEntity ship = ShipEntity.getShip(Client.minecraft.player);

		if (ship != null)
		{
			Quaternion r = ship.getViewRotation(tickDelta).copy();

			if (Client.minecraft.options.perspective == 2)
				r.hamiltonProduct(new Quaternion(0, 180, 0, true));

			r.conjugate();
			matrix.multiply(r);
		}
		else
		{
			matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
			matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
		}
	}
}
