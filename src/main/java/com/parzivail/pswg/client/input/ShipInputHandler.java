package com.parzivail.pswg.client.input;

import com.parzivail.pswg.entity.ship.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

public class ShipInputHandler
{
	@Environment(EnvType.CLIENT)
	public static boolean handle(double cursorDeltaX, double cursorDeltaY)
	{
		var minecraft = MinecraftClient.getInstance();

		var player = minecraft.player;

		assert player != null;

		var ship = ShipEntity.getShip(player);

		if (ship != null)
		{
			ship.acceptMouseInput(cursorDeltaX, cursorDeltaY);
			return true;
		}

		return false;
	}
}
