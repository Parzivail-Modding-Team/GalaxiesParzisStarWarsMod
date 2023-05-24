package com.parzivail.pswg.api;

import com.google.common.collect.ImmutableMap;
import com.parzivail.pswg.character.SpeciesFactory;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.features.blasters.data.BlasterDescriptor;
import com.parzivail.pswg.features.lightsabers.data.LightsaberDescriptor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.util.HashMap;
import java.util.Map;

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

	@FunctionalInterface
	public interface HumanoidCustomizationRegistered
	{
		void humanoidCustomizationRegistered(String id, HumanoidCustomizationOptions options);
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

	public static final Event<HumanoidCustomizationRegistered> HUMANOID_CUSTOMIZATION_REGISTERED = EventFactory.createArrayBacked(HumanoidCustomizationRegistered.class, (id, species) -> {
	}, callbacks -> (id, options) -> {
		for (final var callback : callbacks)
			callback.humanoidCustomizationRegistered(id, options);
	});

	private static boolean isBaked = false;

	private static Map<Identifier, LightsaberDescriptor> lightsaberPresets = new HashMap<>();
	private static Map<Identifier, BlasterDescriptor> blasterPresets = new HashMap<>();
	private static Map<Identifier, SpeciesFactory> speciesPresets = new HashMap<>();
	private static Map<String, HumanoidCustomizationOptions> humanoidCustomizationOptions = new HashMap<>();

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
		humanoidCustomizationOptions.put("humanoid_eyebrows", new HumanoidCustomizationOptions("black"));
		humanoidCustomizationOptions.put("humanoid_scars", new HumanoidCustomizationOptions(SpeciesVariable.NONE));
		humanoidCustomizationOptions.put("humanoid_tattoos", new HumanoidCustomizationOptions(SpeciesVariable.NONE));
		humanoidCustomizationOptions.put("humanoid_hair", new HumanoidCustomizationOptions("1"));
		humanoidCustomizationOptions.put("humanoid_hair_color", new HumanoidCustomizationOptions("37281e"));
		humanoidCustomizationOptions.put("humanoid_clothes_underlayer", new HumanoidCustomizationOptions(SpeciesVariable.NONE));
		humanoidCustomizationOptions.put("humanoid_clothes_top", new HumanoidCustomizationOptions("tatooine_civ1"));
		humanoidCustomizationOptions.put("humanoid_clothes_bottom", new HumanoidCustomizationOptions("tatooine_civ1"));
		humanoidCustomizationOptions.put("humanoid_clothes_belt", new HumanoidCustomizationOptions("tatooine_civ1"));
		humanoidCustomizationOptions.put("humanoid_clothes_boots", new HumanoidCustomizationOptions("tatooine_civ1"));
		humanoidCustomizationOptions.put("humanoid_clothes_gloves", new HumanoidCustomizationOptions("tatooine_civ1"));
		humanoidCustomizationOptions.put("humanoid_clothes_accessories", new HumanoidCustomizationOptions(SpeciesVariable.NONE));
		humanoidCustomizationOptions.put("humanoid_clothes_outerwear", new HumanoidCustomizationOptions("tatooine_civ1"));

		for (var option : humanoidCustomizationOptions.entrySet())
		{
			HUMANOID_CUSTOMIZATION_REGISTERED.invoker().humanoidCustomizationRegistered(option.getKey(), option.getValue());
			SwgSpecies.registerHumanoidOptions(option.getKey(), option.getValue());
		}

		speciesPresets = ImmutableMap.copyOf(speciesPresets);
		SwgSpeciesRegistry.registerAll(speciesPresets);

		lightsaberPresets = ImmutableMap.copyOf(lightsaberPresets);

		for (var preset : blasterPresets.values())
			preset.build();
		blasterPresets = ImmutableMap.copyOf(blasterPresets);

		isBaked = true;
	}
}
