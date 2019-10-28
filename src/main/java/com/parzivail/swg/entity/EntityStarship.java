package com.parzivail.swg.entity;

import com.parzivail.swg.proxy.ShipInputMode;
import net.minecraft.world.World;

public class EntityStarship extends EntityShip
{
	public static final ShipInputMode[] shipInputModes = {
			ShipInputMode.YawCentric, ShipInputMode.RollCentric, ShipInputMode.Repulsor
	};

	public EntityStarship(World worldIn)
	{
		super(worldIn);
	}

	@Override
	protected ShipInputMode[] getAvailableInputModes()
	{
		return shipInputModes;
	}
}
