package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.entity.ship.ShipInput;
import com.parzivail.swg.network.MessageShipInput;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.util.SwgEntityUtil;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by colby on 9/30/2016.
 */
public class KeyHandler
{
	public static void onInput(KeyInputEvent event)
	{
		onInput();
	}

	public static void onInput(MouseInputEvent event)
	{
		onInput();
	}

	private static void onInput()
	{
		StarWarsGalaxy.proxy.checkLeftClickPressed(false);

		if (Keyboard.isKeyDown(Keyboard.KEY_I))
		{
			Client.guiQuestNotification.show();
		}

		EntityShip ship = SwgEntityUtil.getShipRiding(Client.mc.thePlayer);
		if (Client.mc.thePlayer != null && ship != null)
		{
			if (KeybindRegistry.keyShipWingActuate.getIsKeyPressed())
				StarWarsGalaxy.network.sendToServer(new MessageShipInput(ship, ShipInput.WingActuate));
		}
	}

	public static void handleVehicleKeybinds()
	{
		EntityShip ship = SwgEntityUtil.getShipRiding(Client.mc.thePlayer);
		if (Client.mc.thePlayer != null && ship != null)
		{
			//			if ($(KeybindRegistry.keyShipWingActuate))
			//				ship.acceptInput(ShipInput.RollLeft);
		}
	}

	private static boolean $(KeyBinding key)
	{
		return Keyboard.isKeyDown(key.getKeyCode());
	}
}
