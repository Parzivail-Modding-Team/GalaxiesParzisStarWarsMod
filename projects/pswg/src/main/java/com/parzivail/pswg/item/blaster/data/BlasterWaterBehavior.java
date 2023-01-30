package com.parzivail.pswg.item.blaster.data;

public enum BlasterWaterBehavior
{
	CAN_FIRE_UNDERWATER("full"),
	BOLTS_PASS_THROUGH_WATER("bolts"),
	NONE("none"),
	;

	private final String value;

	BlasterWaterBehavior(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
