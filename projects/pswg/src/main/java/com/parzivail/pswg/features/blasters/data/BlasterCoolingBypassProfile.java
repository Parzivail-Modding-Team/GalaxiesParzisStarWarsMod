package com.parzivail.pswg.features.blasters.data;

public class BlasterCoolingBypassProfile
{
	public float primaryBypassTime;
	public float primaryBypassTolerance;
	public float secondaryBypassTime;
	public float secondaryBypassTolerance;

	public BlasterCoolingBypassProfile(float primaryBypassTime, float primaryBypassTolerance, float secondaryBypassTime, float secondaryBypassTolerance)
	{
		this.primaryBypassTime = primaryBypassTime;
		this.primaryBypassTolerance = primaryBypassTolerance;
		this.secondaryBypassTime = secondaryBypassTime;
		this.secondaryBypassTolerance = secondaryBypassTolerance;
	}
}
