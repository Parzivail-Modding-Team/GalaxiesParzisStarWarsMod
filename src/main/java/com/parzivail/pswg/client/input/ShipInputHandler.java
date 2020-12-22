package com.parzivail.pswg.client.input;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.camera.CameraHelper;
import com.parzivail.pswg.client.camera.MutableCameraEntity;
import com.parzivail.pswg.entity.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.Perspective;

public class ShipInputHandler
{
	@Environment(EnvType.CLIENT)
	public static boolean handle(double cursorDeltaX, double cursorDeltaY)
	{
		MinecraftClient mc = Client.minecraft;
		ClientPlayerEntity player = mc.player;

		assert player != null;

		ShipEntity ship = ShipEntity.getShip(player);

		if (ship != null)
		{
			if (mc.options.getPerspective() != Perspective.FIRST_PERSON)
				mc.cameraEntity = CameraHelper.MUTABLE_CAMERA_ENTITY.with(ship, ship.getCamera());
			else
				mc.cameraEntity = ship;

			ship.acceptMouseInput(cursorDeltaX, cursorDeltaY);
			return true;
		}

		if (mc.cameraEntity instanceof MutableCameraEntity || mc.cameraEntity instanceof ShipEntity)
			mc.cameraEntity = player;

		return false;
	}
}
