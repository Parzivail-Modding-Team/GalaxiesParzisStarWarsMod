package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

public class SwgPackets
{
	public static class C2S
	{
		public static final Identifier LightsaberForgeApply = Resources.id("lightsaber_forge_apply");
		public static final Identifier BlasterWorkbenchApply = Resources.id("blaster_workbench_apply");
		public static final Identifier SetOwnSpecies = Resources.id("set_own_species");
		public static final Identifier PlayerLeftClickItem = Resources.id("player_use_left");
		public static final Identifier PlayerItemAction = Resources.id("item_action");
		public static final Identifier ShipFire = Resources.id("ship_fire");
		public static final Identifier ShipRotation = Resources.id("srot");
		public static final Identifier ShipControls = Resources.id("sctrl");
	}

	public static class S2C
	{
		public static final Identifier SyncBlasters = Resources.id("sync_blasters");
		public static final Identifier SyncLightsabers = Resources.id("sync_lightsabers");
		public static final Identifier SyncSpecies = Resources.id("sync_species");
		public static final Identifier PlayerEvent = Resources.id("pevt");
		public static final Identifier WorldEvent = Resources.id("wevt");
		public static final Identifier SyncBlockToClient = Resources.id("csync");
		public static final Identifier PreciseEntityVelocityUpdate = Resources.id("pevel");
		public static final Identifier PreciseEntitySpawn = Resources.id("pesp");
		public static final Identifier OpenEntityInventory = Resources.id("entity_inventory");
	}
}
