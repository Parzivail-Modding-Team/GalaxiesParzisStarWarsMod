package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.ShipInput;
import com.parzivail.util.entity.EntityUtils;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by colby on 9/30/2016.
 */
public class KeyHandler
{
	public static void onKeyInput(InputEvent.KeyInputEvent event)
	{
	}

	public static void handleVehicleMovement()
	{
		BasicFlightModel ship = EntityUtils.getShipRiding(StarWarsGalaxy.mc.thePlayer);
		if (StarWarsGalaxy.mc.thePlayer != null && ship != null)
		{
			if ($(StarWarsGalaxy.mc.gameSettings.keyBindLeft))
				ship.acceptInput(ShipInput.RollLeft);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindRight))
				ship.acceptInput(ShipInput.RollRight);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindForward))
				ship.acceptInput(ShipInput.PitchDown);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindBack))
				ship.acceptInput(ShipInput.PitchUp);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindJump))
				ship.acceptInput(ShipInput.ThrottleUp);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindSprint))
				ship.acceptInput(ShipInput.ThrottleDown);
		}
	}

	private static boolean $(KeyBinding key)
	{
		return Keyboard.isKeyDown(key.getKeyCode());
	}
}
