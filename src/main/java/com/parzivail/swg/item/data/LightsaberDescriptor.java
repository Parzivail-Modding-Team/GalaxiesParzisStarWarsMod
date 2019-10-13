package com.parzivail.swg.item.data;

public class LightsaberDescriptor
{
	public static final LightsaberDescriptor BLANK = new LightsaberDescriptor();

	public int bladeColor = 0xFF00FF00;
	public int coreColor = 0xFFFFFFFF;
	public float bladeLength = 1.5f;
	public boolean unstable;
}
