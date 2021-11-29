package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

public class SwgPackets
{
	public static class C2S
	{
		public static final Identifier PacketLightsaberForgeApply = Resources.id("lightsaber_forge_apply");
		public static final Identifier PacketSetOwnSpecies = Resources.id("set_own_species");
		public static final Identifier PacketPlayerLeftClickItem = Resources.id("player_use_left");
		public static final Identifier PacketPlayerItemAction = Resources.id("item_action");
		public static final Identifier PacketShipFire = Resources.id("ship_fire");
		public static final Identifier PacketShipRotation = Resources.id("ship_rotation");
		public static final Identifier PacketShipControls = Resources.id("ship_controls");
	}

	public static class S2C
	{
		public static final Identifier PacketSyncBlasters = Resources.id("sync_blasters");
		public static final Identifier PacketSyncLightsabers = Resources.id("sync_lightsabers");
		public static final Identifier PacketPlayerEvent = Resources.id("player_event");
		public static final Identifier PacketWorldEvent = Resources.id("world_event");
		public static final Identifier PacketClientSync = Resources.id("client_sync");
	}
}
