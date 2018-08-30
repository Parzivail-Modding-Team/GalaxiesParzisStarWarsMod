package com.parzivail.swg.registry;

import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.entity.EntitySmokeGrenade;
import com.parzivail.swg.entity.EntityThermalDetonator;
import com.parzivail.swg.npc.NpcJawa;
import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.ship.EntitySeat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.util.entity.EntityUtils;

/**
 * Created by colby on 12/26/2017.
 */
public class EntityRegister
{
	public static void register()
	{
		EntityUtils.registerEntity(VehicleT65.class, "t65");
		EntityUtils.registerEntity(EntitySeat.class, "seat");

		EntityUtils.registerEntity(EntityBlasterBolt.class, "blasterBolt");

		EntityUtils.registerEntity(EntityThermalDetonator.class, "thermalDetonator");
		EntityUtils.registerEntity(EntitySmokeGrenade.class, "smokeGrenade");

		EntityUtils.registerWithSpawnEgg(NpcMerchant.class, "testNpc", 0x6AC8D8, 0x6BD67B);
		EntityUtils.registerWithSpawnEgg(NpcJawa.class, "jawa", 0xFF0000, 0x9B6C00);
	}
}
