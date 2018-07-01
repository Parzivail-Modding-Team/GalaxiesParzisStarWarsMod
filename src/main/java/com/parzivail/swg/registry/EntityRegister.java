package com.parzivail.swg.registry;

import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.entity.EntitySmokeGrenade;
import com.parzivail.swg.entity.EntityThermalDetonator;
import com.parzivail.swg.entity.multipart.EntityMultipart;
import com.parzivail.swg.entity.multipart.EntityPartTest;
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

		EntityUtils.registerEntity(EntityBlasterBolt.class, "blasterBolt");
		EntityUtils.registerEntity(EntityThermalDetonator.class, "thermalDetonator");
		EntityUtils.registerEntity(EntitySmokeGrenade.class, "smokeGrenade");

		EntityUtils.registerEntity(EntityMultipart.class, "multitest");
		EntityUtils.registerEntity(EntityPartTest.class, "parttest");
	}
}
