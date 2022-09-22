package com.parzivail.pswg.api;

import com.google.common.collect.ImmutableMap;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PswgContent
{
	private static Map<Identifier, LightsaberDescriptor> lightsaberPresets = new HashMap<>();
	private static Map<Identifier, BlasterDescriptor> blasterPresets = new HashMap<>();

	public static void registerLightsaberPreset(LightsaberDescriptor... descriptors)
	{
		for (var entry : descriptors)
			lightsaberPresets.put(entry.id(), entry);
	}

	public static Map<Identifier, LightsaberDescriptor> getLightsaberPresets()
	{
		return lightsaberPresets;
	}

	public static void registerBlasterPreset(BlasterDescriptor... descriptors)
	{
		for (var entry : descriptors)
			blasterPresets.put(entry.id, entry);
	}

	public static Map<Identifier, BlasterDescriptor> getBlasterPresets()
	{
		return blasterPresets;
	}

	public static BlasterDescriptor getBlasterPreset(Identifier key)
	{
		return blasterPresets.get(key);
	}

	public static BlasterDescriptor assertBlasterPreset(Identifier key)
	{
		var data = getBlasterPreset(key);
		if (data != null)
			return data;

		var keyName = key == null ? "[null]" : '"' + key.toString() + '"';
		var j = CrashReport.create(new NullPointerException("Cannot get blaster descriptor for unknown key " + keyName), "Getting blaster descriptor");

		var k = j.addElement("Blaster Manager Data");
		k.add("Defined keys", PswgContent::getBlasterDataString);

		throw new CrashException(j);
	}

	private static String getBlasterDataString()
	{
		if (blasterPresets == null)
			return "null";
		return blasterPresets.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "));
	}

	public static void bake()
	{
		lightsaberPresets = ImmutableMap.copyOf(lightsaberPresets);
		blasterPresets = ImmutableMap.copyOf(blasterPresets);
	}
}
