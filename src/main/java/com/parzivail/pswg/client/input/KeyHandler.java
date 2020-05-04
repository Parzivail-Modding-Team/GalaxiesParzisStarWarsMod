package com.parzivail.pswg.client.input;

import com.parzivail.pswg.entity.ShipEntity;
import net.minecraft.client.MinecraftClient;

import java.util.EnumSet;

public class KeyHandler
{
	public static void handle(MinecraftClient mc)
	{
		assert mc.player != null;

		ShipEntity ship = ShipEntity.getShip(mc.player);

		if (ship != null)
		{
			EnumSet<ShipControls> controls = EnumSet.noneOf(ShipControls.class);

			if (mc.options.keyForward.isPressed())
				controls.add(ShipControls.THROTTLE_UP);

			if (mc.options.keyBack.isPressed())
				controls.add(ShipControls.THROTTLE_DOWN);

			ship.acceptControlInput(controls);
		}
	}
}
