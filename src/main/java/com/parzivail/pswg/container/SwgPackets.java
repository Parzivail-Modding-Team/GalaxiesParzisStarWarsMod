package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

public class SwgPackets
{
	public static class C2S
	{
		public static final Identifier PacketShipRotation = Resources.identifier("ship_rotation");
		public static final Identifier PacketShipControls = Resources.identifier("ship_controls");
	}
}
