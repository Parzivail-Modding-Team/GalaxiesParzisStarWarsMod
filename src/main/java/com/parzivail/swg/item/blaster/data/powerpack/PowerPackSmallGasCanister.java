package com.parzivail.swg.item.blaster.data.powerpack;

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
