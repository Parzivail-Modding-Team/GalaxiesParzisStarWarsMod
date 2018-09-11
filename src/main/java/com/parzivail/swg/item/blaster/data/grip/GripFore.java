package com.parzivail.swg.item.blaster.data.grip;

public class GripFore extends BlasterGrip
{
	public GripFore()
	{
		super("fore", 200);
	}

	@Override
	public float getVerticalRecoilReduction()
	{
		return 0.4f;
	}

	@Override
	public float getHorizontalRecoilReduction()
	{
		return 0.6f;
	}
}
