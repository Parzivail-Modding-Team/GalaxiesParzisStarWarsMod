package com.parzivail.pswg.features.lightsabers.data;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public final class LightsaberDescriptor
{
	public final Identifier id;

	public String ownerName;
	public int bladeColor;
	public LightsaberBladeType bladeType;

	private HashMap<String, Float> bladeLengthMap;

	public LightsaberDescriptor(Identifier id, String ownerName, int bladeColor, LightsaberBladeType bladeType)
	{
		this.id = id;
		this.ownerName = ownerName;
		this.bladeColor = bladeColor;
		this.bladeType = bladeType;

		bladeLengthMap = new HashMap<>();
	}

	public LightsaberDescriptor bladeLength(String name, float length)
	{
		bladeLengthMap.put(name, length);
		return this;
	}

	public float getBladeLength(String name)
	{
		return bladeLengthMap.getOrDefault(name, 1f);
	}
}
