package com.parzivail.pswg.api;

import com.google.common.collect.ImmutableList;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PswgContent
{
	private static List<LightsaberDescriptor> lightsaberPresets = new ArrayList<>();

	public static void registerLightsaberPreset(LightsaberDescriptor... descriptors)
	{
		Collections.addAll(lightsaberPresets, descriptors);
	}

	public static List<LightsaberDescriptor> getLightsaberPresets()
	{
		return lightsaberPresets;
	}

	public static void bake()
	{
		lightsaberPresets = ImmutableList.copyOf(lightsaberPresets);
	}
}
