package com.parzivail.pswg.client.camera;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.entity.ShipEntity;
import net.minecraft.client.options.Perspective;
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
			Quaternion r = new Quaternion(ship.getViewRotation(tickDelta));

			if (Client.minecraft.options.getPerspective() == Perspective.THIRD_PERSON_FRONT)
				r.hamiltonProduct(new Quaternion(Vector3f.POSITIVE_Y, 180, true));

			r.conjugate();
			matrix.multiply(r);
		}
		else
		{
			matrix.multiply(new Quaternion(Vector3f.POSITIVE_X, camera.getPitch(), true));
			matrix.multiply(new Quaternion(Vector3f.POSITIVE_Y, camera.getYaw() + 180.0F, true));
		}
	}
}
