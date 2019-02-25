package com.parzivail.swg.entity.ship;

import com.parzivail.swg.ship.ShipData;
import net.minecraft.world.World;

public class EntityT65 extends EntityShip
{
	private static final ShipData shipData;

	static
	{
		shipData = new ShipData();
	}

	@Override
	public ShipData getData()
	{
		return shipData;
	}

	public EntityT65(World worldIn)
	{
		super(worldIn);
	}
}
