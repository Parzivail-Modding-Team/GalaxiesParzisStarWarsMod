package com.parzivail.swg.item.blaster.data.grip;

public class GripNone extends BlasterGrip
{
	public GripNone()
	{
		super("none", 0);
	}

	@Override
	public float getVerticalRecoilReduction()
	{
		return 0;
	}

	@Override
	public float getHorizontalRecoilReduction()
	{
		return 0;
	}
}
