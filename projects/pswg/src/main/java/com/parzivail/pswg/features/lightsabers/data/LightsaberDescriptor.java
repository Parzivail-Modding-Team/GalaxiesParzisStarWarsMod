package com.parzivail.pswg.features.lightsabers.data;

import net.minecraft.util.Identifier;

public final class LightsaberDescriptor
{
	public final Identifier id;

	public String ownerName;
	public int bladeColor;
	public LightsaberBladeType bladeType;

	public LightsaberDescriptor(Identifier id, String ownerName, int bladeColor, LightsaberBladeType bladeType)
	{
		this.id = id;
		this.ownerName = ownerName;
		this.bladeColor = bladeColor;
		this.bladeType = bladeType;
	}
}
