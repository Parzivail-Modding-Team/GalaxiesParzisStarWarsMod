package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.network.MessageItemLeftClick;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.ShipInput;
import com.parzivail.util.entity.EntityUtils;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
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
		ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

		if (KeybindRegistry.keyAttack.interceptedIsPressed() && heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor)
		{
			((ILeftClickInterceptor)heldItem.getItem()).onItemLeftClick(heldItem, Client.mc.thePlayer.worldObj, Client.mc.thePlayer);
			StarWarsGalaxy.network.sendToServer(new MessageItemLeftClick(Client.mc.thePlayer));
		}

		if (KeybindRegistry.keyDebug.getIsKeyPressed())
		{
		}
	}

	public static void handleVehicleMovement()
	{
		BasicFlightModel ship = EntityUtils.getShipRiding(Client.mc.thePlayer);
		if (Client.mc.thePlayer != null && ship != null)
		{
			if ($(Client.mc.gameSettings.keyBindLeft))
				ship.acceptInput(ShipInput.RollLeft);

			if ($(Client.mc.gameSettings.keyBindRight))
				ship.acceptInput(ShipInput.RollRight);

			if ($(Client.mc.gameSettings.keyBindForward))
				ship.acceptInput(ShipInput.PitchDown);

			if ($(Client.mc.gameSettings.keyBindBack))
				ship.acceptInput(ShipInput.PitchUp);

			if ($(Client.mc.gameSettings.keyBindJump))
				ship.acceptInput(ShipInput.ThrottleUp);

			if ($(Client.mc.gameSettings.keyBindSprint))
				ship.acceptInput(ShipInput.ThrottleDown);
		}
	}

	private static boolean $(KeyBinding key)
	{
		return Keyboard.isKeyDown(key.getKeyCode());
	}
}
