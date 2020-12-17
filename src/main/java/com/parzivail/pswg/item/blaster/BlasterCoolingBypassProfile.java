package com.parzivail.pswg.item.blaster;

public class BlasterCoolingBypassProfile
{
	public static final BlasterCoolingBypassProfile NONE = new BlasterCoolingBypassProfile(0, 0, 0, 0);
	public static final BlasterCoolingBypassProfile STANDARD = new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.1f);

	public final float primaryBypassTime;
	public final float primaryBypassTolerance;
	public final float secondaryBypassTime;
	public final float secondaryBypassTolerance;

	public BlasterCoolingBypassProfile(float primaryBypassTime, float primaryBypassTolerance, float secondaryBypassTime, float secondaryBypassTolerance)
	{
		this.primaryBypassTime = primaryBypassTime;
		this.primaryBypassTolerance = primaryBypassTolerance;
		this.secondaryBypassTime = secondaryBypassTime;
		this.secondaryBypassTolerance = secondaryBypassTolerance;
	}
}
