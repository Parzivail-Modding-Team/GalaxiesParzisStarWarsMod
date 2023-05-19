package com.parzivail.pswg.api;

import com.google.common.collect.ImmutableMap;
import com.parzivail.pswg.character.SpeciesFactory;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.data.BlasterDescriptor;
import com.parzivail.pswg.features.lightsabers.data.LightsaberDescriptor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PswgContent
{
	@FunctionalInterface
	public interface LightsaberRegistered
	{
		void lightsaberRegistered(Identifier id, LightsaberDescriptor descriptor);
	}

	@FunctionalInterface
	public interface BlasterRegistered
	{
		void blasterRegistered(Identifier id, BlasterDescriptor descriptor);
	}

	@FunctionalInterface
	public interface SpeciesRegistered
	{
		void speciesRegistered(Identifier id, SpeciesFactory species);
	}

	public static final Event<LightsaberRegistered> LIGHTSABER_REGISTERED = EventFactory.createArrayBacked(LightsaberRegistered.class, (id, descriptor) -> {
	}, callbacks -> (id, descriptor) -> {
		for (final var callback : callbacks)
			callback.lightsaberRegistered(id, descriptor);
	});

	public static final Event<BlasterRegistered> BLASTER_REGISTERED = EventFactory.createArrayBacked(BlasterRegistered.class, (id, descriptor) -> {
	}, callbacks -> (id, descriptor) -> {
		for (final var callback : callbacks)
			callback.blasterRegistered(id, descriptor);
	});

	public static final Event<SpeciesRegistered> SPECIES_REGISTERED = EventFactory.createArrayBacked(SpeciesRegistered.class, (id, species) -> {
	}, callbacks -> (id, species) -> {
		for (final var callback : callbacks)
			callback.speciesRegistered(id, species);
	});

	private static boolean isBaked = false;

	private static Map<Identifier, LightsaberDescriptor> lightsaberPresets = new HashMap<>();
	private static Map<Identifier, BlasterDescriptor> blasterPresets = new HashMap<>();
	private static Map<Identifier, SpeciesFactory> speciesPresets = new HashMap<>();

	public static void registerLightsaberPreset(LightsaberDescriptor... descriptors)
	{
		checkBaked();
		for (var entry : descriptors)
		{
			LIGHTSABER_REGISTERED.invoker().lightsaberRegistered(entry.id, entry);
			lightsaberPresets.put(entry.id, entry);
		}
	}

	public static Map<Identifier, LightsaberDescriptor> getLightsaberPresets()
	{
		return lightsaberPresets;
	}

	public static LightsaberDescriptor getLightsaberPreset(Identifier key)
	{
		return lightsaberPresets.get(key);
	}

	public static LightsaberDescriptor assertLightsaberPreset(Identifier key)
	{
		var data = getLightsaberPreset(key);
		if (data != null)
			return data;

		var keyName = key == null ? "[null]" : '"' + key.toString() + '"';
		var j = CrashReport.create(new NullPointerException("Cannot get lightsaber descriptor for unknown key " + keyName), "Getting lightsaber descriptor");

		var k = j.addElement("Lightsaber Manager Data");
		k.add("Defined keys", PswgContent::getLightsaberDataString);

		throw new CrashException(j);
	}

	private static String getLightsaberDataString()
	{
		if (lightsaberPresets == null)
			return "null";
		return lightsaberPresets.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "));
	}

	public static void registerBlasterPreset(BlasterDescriptor... descriptors)
	{
		checkBaked();
		for (var entry : descriptors)
		{
			BLASTER_REGISTERED.invoker().blasterRegistered(entry.id, entry);
			blasterPresets.put(entry.id, entry);
		}
	}

	public static Map<Identifier, BlasterDescriptor> getBlasterPresets()
	{
		return blasterPresets;
	}

	public static void registerSpecies(SpeciesFactory... species)
	{
		checkBaked();
		for (var entry : species)
		{
			SPECIES_REGISTERED.invoker().speciesRegistered(entry.getSlug(), entry);
			speciesPresets.put(entry.getSlug(), entry);
		}
	}

	public static Map<Identifier, SpeciesFactory> getSpecies()
	{
		return speciesPresets;
	}

	private static void checkBaked()
	{
		if (!isBaked)
			return;

		throw new CrashException(CrashReport.create(new IllegalStateException("Cannot add content after registry is frozen"), "Updating PSWG content registry"));
	}

	public static void bake()
	{
		speciesPresets = ImmutableMap.copyOf(speciesPresets);
		SwgSpeciesRegistry.registerAll(speciesPresets);

		lightsaberPresets = ImmutableMap.copyOf(lightsaberPresets);

		for (var preset : blasterPresets.values())
			preset.build();
		blasterPresets = ImmutableMap.copyOf(blasterPresets);

		BlasterItem.bakeAttributeModifiers(blasterPresets);

		isBaked = true;
	}
}
