package com.parzivail.pswg.client.input;

import com.parzivail.pswg.entity.ship.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ShipInputHandler
{
	@Environment(EnvType.CLIENT)
	public static boolean handle(double cursorDeltaX, double cursorDeltaY)
	{
		MinecraftClient minecraft = MinecraftClient.getInstance();

		ClientPlayerEntity player = minecraft.player;

		assert player != null;

		ShipEntity ship = ShipEntity.getShip(player);

		if (ship != null)
		{
			ship.acceptMouseInput(cursorDeltaX, cursorDeltaY);
			return true;
		}

		return false;
	}
}
