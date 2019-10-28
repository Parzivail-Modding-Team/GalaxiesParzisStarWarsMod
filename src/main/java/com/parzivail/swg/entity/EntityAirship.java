package com.parzivail.swg.entity;

import com.parzivail.swg.proxy.ShipInputMode;
import net.minecraft.world.World;

public class EntityAirship extends EntityShip
{
	public static final ShipInputMode[] shipInputModes = {
			ShipInputMode.Repulsor
	};

	public EntityAirship(World worldIn)
	{
		super(worldIn);
	}

	@Override
	protected ShipInputMode[] getAvailableInputModes()
	{
		return shipInputModes;
	}
}
