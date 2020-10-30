package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

public class SwgPackets
{
	public static class C2S
	{
		public static final Identifier PacketPlayerLeftClickItem = Resources.identifier("player_use_left");
		public static final Identifier PacketPlayerLightsaberToggle = Resources.identifier("player_lightsaber_toggle");
		public static final Identifier PacketShipRotation = Resources.identifier("ship_rotation");
		public static final Identifier PacketShipControls = Resources.identifier("ship_controls");
	}
}
