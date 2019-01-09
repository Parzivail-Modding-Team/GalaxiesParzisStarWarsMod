package com.parzivail.swg.registry;

import com.parzivail.swg.entity.*;
import com.parzivail.swg.mob.MobGizka;
import com.parzivail.swg.npc.NpcJawa;
import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.ship.EntitySeat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.swg.util.SwgEntityUtil;

/**
 * Created by colby on 12/26/2017.
 */
public class EntityRegister
{
	public static void register()
	{
		SwgEntityUtil.registerEntity(VehicleT65.class, "t65");
		SwgEntityUtil.registerEntity(EntitySeat.class, "seat");

		SwgEntityUtil.registerEntity(EntityShipParentTest.class, "espt");

		SwgEntityUtil.registerEntity(EntityChair.class, "chair");

		SwgEntityUtil.registerEntity(EntityBlasterBolt.class, "blasterBolt");

		SwgEntityUtil.registerEntity(EntityThermalDetonator.class, "thermalDetonator");
		SwgEntityUtil.registerEntity(EntitySmokeGrenade.class, "smokeGrenade");

		SwgEntityUtil.registerWithSpawnEgg(NpcMerchant.class, "testNpc", 0x6AC8D8, 0x6BD67B);
		SwgEntityUtil.registerWithSpawnEgg(NpcJawa.class, "jawa", 0xFF0000, 0x9B6C00);

		SwgEntityUtil.registerWithSpawnEgg(MobGizka.class, "gizka", 0xFFC291, 0x72BC4D);
	}
}
