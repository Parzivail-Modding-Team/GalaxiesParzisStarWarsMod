package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.Seat;
import com.parzivail.swg.ship.ShipInput;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.common.Pair;
import com.parzivail.util.entity.EntityUtils;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by colby on 9/30/2016.
 */
public class KeyHandler
{
	public static void onInput(InputEvent.KeyInputEvent event)
	{
		onInput();
	}

	public static void onInput(InputEvent.MouseInputEvent event)
	{
		onInput();
	}

	private static void onInput()
	{
		if (KeybindRegistry.keyAttack.interceptedIsPressed())
			Lumberjack.log("Fire");
	}

	public static void handleVehicleMovement()
	{
		Pair<BasicFlightModel, Seat> pair = EntityUtils.getShipRiding(StarWarsGalaxy.mc.thePlayer);
		if (StarWarsGalaxy.mc.thePlayer != null && pair != null && pair.left != null && pair.right.getIdx() == 0)
		{
			if ($(StarWarsGalaxy.mc.gameSettings.keyBindLeft))
				pair.right.acceptInput(ShipInput.RollLeft);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindRight))
				pair.right.acceptInput(ShipInput.RollRight);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindForward))
				pair.right.acceptInput(ShipInput.PitchDown);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindBack))
				pair.right.acceptInput(ShipInput.PitchUp);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindJump))
				pair.right.acceptInput(ShipInput.ThrottleUp);

			if ($(StarWarsGalaxy.mc.gameSettings.keyBindSprint))
				pair.right.acceptInput(ShipInput.ThrottleDown);
		}
	}

	private static boolean $(KeyBinding key)
	{
		return Keyboard.isKeyDown(key.getKeyCode());
	}
}
