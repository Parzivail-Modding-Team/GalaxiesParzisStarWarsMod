package com.parzivail.pswg.client.input;

import com.parzivail.pswg.entity.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ShipInputHandler
{
	@Environment(EnvType.CLIENT)
	public static boolean handle(double cursorDeltaX, double cursorDeltaY)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;

		assert player != null;

		ShipEntity ship = ShipEntity.getShip(player);
		if (ship != null)
		{
			mc.cameraEntity = ship.camera;
			ship.acceptInput(cursorDeltaX, cursorDeltaY);
			return true;
		}

		mc.cameraEntity = player;

		return false;
	}
}
