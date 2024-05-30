package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

public class SwgPackets
{
	public static class C2S
	{
		public static final Identifier LightsaberForgeApply = Resources.id("lightsaber_forge_apply");
		public static final Identifier BlasterWorkbenchApply = Resources.id("blaster_workbench_apply");
		public static final Identifier RequestCustomizeSelf = Resources.id("open_character_customizer");
		public static final Identifier SetOwnSpecies = Resources.id("set_own_species");
		public static final Identifier PlayerLeftClickItem = Resources.id("player_use_left");
		public static final Identifier PlayerItemAction = Resources.id("item_action");
		public static final Identifier ShipFire = Resources.id("ship_fire");
		public static final Identifier ShipRotation = Resources.id("srot");
		public static final Identifier ShipControls = Resources.id("sctrl");
		public static final Identifier JetpackControls = Resources.id("jctrl");
		public static final Identifier TogglePatrolPosture = Resources.id("toggle_patrol_posture");
	}

	public static class S2C
	{
		public static final Identifier SyncBlockToClient = Resources.id("csync");
		public static final Identifier PreciseEntityVelocityUpdate = Resources.id("pevel");
		public static final Identifier PreciseEntitySpawn = Resources.id("pesp");
		public static final Identifier OpenEntityInventory = Resources.id("entity_inventory");
		public static final Identifier AccumulateRecoil = Resources.id("recoil");
		public static final Identifier BlasterHit = Resources.id("blaster_hit");
		public static final Identifier FragmentationGrenadeExplode = Resources.id("fragmentation_grenade_explode");
		public static final Identifier PlayerSocketPyro = Resources.id("player_sparks");
		public static final Identifier OpenCharacterCustomizer = Resources.id("open_character_customizer");
	}
}
