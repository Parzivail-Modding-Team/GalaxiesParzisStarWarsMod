package com.parzivail.pswg.features.lightsabers.data;

import com.parzivail.pswg.Resources;

import java.util.HashMap;

public enum LightsaberBladeType
{
	DEFAULT("default"),
	DARKSABER("darksaber"),
	BRICK("brick");

	public static final HashMap<String, LightsaberBladeType> TYPES = new HashMap<>();

	static
	{
		for (var value : values())
			TYPES.put(value.id, value);
	}

	private final String id;

	LightsaberBladeType(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public String getLangKey()
	{
		return Resources.screen("lightsaber.blade." + id);
	}
}
