package com.parzivail.swg.weapon.blastermodule.grip;

public class GripNone extends BlasterGrip
{
	public GripNone()
	{
		super("none");
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
