package com.parzivail.swg.ship;

import com.parzivail.swg.entity.ShipType;

/**
 * Created by colby on 12/30/2017.
 */
public class ShipData
{
	public float maxThrottle = 2;
	public float acceleration = 0.1f;

	public static ShipData create(ShipType type)
	{
		// TODO: ship data per ship type
		return new ShipData();
	}
}
