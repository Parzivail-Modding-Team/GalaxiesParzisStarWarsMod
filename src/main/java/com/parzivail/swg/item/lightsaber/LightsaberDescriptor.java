package com.parzivail.swg.item.lightsaber;

import com.parzivail.util.ui.GLPalette;

public class LightsaberDescriptor
{
	public static final LightsaberDescriptor BLANK = new LightsaberDescriptor();

	public int bladeColor = GLPalette.GREEN;
	public int coreColor = GLPalette.WHITE;
	public float bladeLength = 3;
	public boolean unstable;
}
