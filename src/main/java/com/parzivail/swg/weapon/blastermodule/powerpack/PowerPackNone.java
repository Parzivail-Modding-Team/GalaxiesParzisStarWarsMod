package com.parzivail.swg.weapon.blastermodule.powerpack;

public class PowerPackNone extends BlasterPowerPack
{
	public PowerPackNone()
	{
		super("none", 0);
	}

	@Override
	public int getNumShots()
	{
		return 0;
	}
}
