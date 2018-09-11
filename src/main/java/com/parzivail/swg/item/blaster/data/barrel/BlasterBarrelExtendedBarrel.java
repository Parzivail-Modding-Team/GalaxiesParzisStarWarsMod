package com.parzivail.swg.item.blaster.data.barrel;

public class BlasterBarrelExtendedBarrel extends BlasterBarrel
{
	public BlasterBarrelExtendedBarrel()
	{
		super("extended", 450);
	}

	@Override
	public float getHorizontalRecoilReduction()
	{
		return 0;
	}

	@Override
	public float getHorizontalSpreadReduction()
	{
		return 0;
	}

	@Override
	public float getVerticalRecoilReduction()
	{
		return 0;
	}

	@Override
	public float getVerticalSpreadReduction()
	{
		return 0;
	}

	@Override
	public float getNoiseReduction()
	{
		return 0.8f;
	}

	@Override
	public float getRangeIncrease()
	{
		return 0.5f;
	}
}
