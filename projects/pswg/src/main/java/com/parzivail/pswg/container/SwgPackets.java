package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.network.*;
import com.parzivail.util.network.PreciseEntitySpawnS2CPacket;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SwgPackets
{
	public static class C2S
	{
		public static final Identifier LightsaberForgeApply = new CustomPayload.Id<>(Resources.id("lightsaber_forge_apply"));
		public static final Identifier BlasterWorkbenchApply = new CustomPayload.Id<>(Resources.id("blaster_workbench_apply"));
		public static final CustomPayload.Id<UnitC2SPacket> RequestCustomizeSelf = new CustomPayload.Id<>(Resources.id("open_character_customizer")); // unit
		public static final CustomPayload.Id<SetOwnSpeciesC2SPacket> SetOwnSpecies = new CustomPayload.Id<>(Resources.id("set_own_species"));
		public static final CustomPayload.Id<PlayerItemLeftClickC2SPacket> PlayerLeftClickItem = new CustomPayload.Id<>(Resources.id("player_use_left"));
		public static final CustomPayload.Id<PlayerItemActionC2SPacket> PlayerItemAction = new CustomPayload.Id<>(Resources.id("item_action"));
		public static final CustomPayload.Id<UnitC2SPacket> ShipFire = new CustomPayload.Id<>(Resources.id("ship_fire")); // unit
		public static final CustomPayload.Id<ShipRotationC2SPacket> ShipRotation = new CustomPayload.Id<>(Resources.id("srot"));
		public static final CustomPayload.Id<ShipControlsC2SPacket> ShipControls = new CustomPayload.Id<>(Resources.id("sctrl"));
		public static final CustomPayload.Id<JetpackControlsC2SPacket> JetpackControls = new CustomPayload.Id<>(Resources.id("jctrl"));
		public static final CustomPayload.Id<UnitC2SPacket> TogglePatrolPosture = new CustomPayload.Id<>(Resources.id("toggle_patrol_posture")); // unit
	}

	public static class S2C
	{
		public static final CustomPayload.Id<PreciseEntityVelocityUpdateS2CPacket> PreciseEntityVelocityUpdate = new CustomPayload.Id<>(Resources.id("pevel"));
		public static final CustomPayload.Id<PreciseEntitySpawnS2CPacket> PreciseEntitySpawn = new CustomPayload.Id<>(Resources.id("pesp"));
		public static final CustomPayload.Id<OpenEntityInventoryS2CPacket> OpenEntityInventory = new CustomPayload.Id<>(Resources.id("entity_inventory"));
		public static final CustomPayload.Id<AccumulateRecoilS2CPacket> AccumulateRecoil = new CustomPayload.Id<>(Resources.id("recoil"));
		public static final CustomPayload.Id<BlasterHitS2CPacket> BlasterHit = new CustomPayload.Id<>(Resources.id("blaster_hit"));
		public static final CustomPayload.Id<PlayerSocketSparksS2CPacket> PlayerSocketPyro = new CustomPayload.Id<>(Resources.id("player_sparks"));
		public static final Identifier OpenCharacterCustomizer = Resources.id("open_character_customizer");
	}
}
