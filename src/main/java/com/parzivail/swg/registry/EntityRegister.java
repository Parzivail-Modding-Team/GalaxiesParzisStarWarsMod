package com.parzivail.swg.registry;

import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.ship.Seat;
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
		EntityUtils.registerEntity(Seat.class, "seat");
		EntityUtils.registerEntity(EntityBlasterBolt.class, "blasterBolt");
	}
}
