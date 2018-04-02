package com.parzivail.swg.weapon.blastermodule.powerpack;

public class PowerPackSmallGasCanister extends BlasterPowerPack
{
	public PowerPackSmallGasCanister()
	{
		super("smallGasCanister", 0);
	}

	@Override
	public int getNumShots()
	{
		return 40;
	}
}
