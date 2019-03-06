package com.parzivail.swg.entity.ship;

import net.minecraft.world.World;

public class EntityScootEmAround extends EntityShip
{
	private static final ShipData shipData;

	static
	{
		shipData = new ShipData();
		shipData.isAirVehicle = false;
		shipData.maxThrottle = 1f;
		shipData.verticalGroundingOffset = 1.5f;
	}

	public EntityScootEmAround(World worldIn)
	{
		super(worldIn);
		stepHeight = 5;
	}

	@Override
	public ShipData getData()
	{
		return shipData;
	}
}
