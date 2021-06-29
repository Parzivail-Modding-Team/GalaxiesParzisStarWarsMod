package com.parzivail.pswg.client.input;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.ship.ShipEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.util.EnumSet;

public class KeyHandler
{
	public static void handle(MinecraftClient mc)
	{
		if (mc.player == null)
			return;

		if (Client.KEY_SPECIES_SELECT.wasPressed())
		{
			// TODO: reimplement species select screen
			// Client.minecraft.openScreen(new SpeciesSelectScreen(Client.minecraft.currentScreen));
		}

		if (Client.KEY_LIGHTSABER_TOGGLE.wasPressed())
		{
			ClientPlayNetworking.send(SwgPackets.C2S.PacketPlayerLightsaberToggle, new PacketByteBuf(Unpooled.buffer()));
		}

		ShipEntity ship = ShipEntity.getShip(mc.player);

		if (ship != null)
		{
			EnumSet<ShipControls> controls = EnumSet.noneOf(ShipControls.class);

			if (mc.options.keyForward.isPressed())
				controls.add(ShipControls.THROTTLE_UP);

			if (mc.options.keyBack.isPressed())
				controls.add(ShipControls.THROTTLE_DOWN);

			if (mc.options.keyRight.isPressed())
				controls.add(ShipControls.SPECIAL1);

			if (mc.options.keyLeft.isPressed())
				controls.add(ShipControls.SPECIAL2);

			ship.acceptControlInput(controls);
		}
	}
}
