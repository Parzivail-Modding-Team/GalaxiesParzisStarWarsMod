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
		RegisterResult lightsaberRegistered(Identifier id, LightsaberDescriptor descriptor);
	}

	@FunctionalInterface
	public interface BlasterRegistered
	{
		RegisterResult blasterRegistered(Identifier id, BlasterDescriptor descriptor);
	}

	@FunctionalInterface
	public interface SpeciesRegistered
	{
		RegisterResult speciesRegistered(Identifier id, SpeciesFactory species);
	}

	@FunctionalInterface
	public interface CollectHumanoidCustomization
	{
		void collectHumanoidCustomization(String id, HumanoidCustomizationOptions options);
	}

	public static final Event<LightsaberRegistered> LIGHTSABER_REGISTERED = EventFactory.createArrayBacked(
			LightsaberRegistered.class,
			(id, descriptor) -> RegisterResult.Pass,
			callbacks -> (id, descriptor) -> {
				for (final var callback : callbacks)
					if (callback.lightsaberRegistered(id, descriptor) == RegisterResult.Cancel)
						return RegisterResult.Cancel;
				return RegisterResult.Pass;
			}
	);

	public static final Event<BlasterRegistered> BLASTER_REGISTERED = EventFactory.createArrayBacked(
			BlasterRegistered.class,
			(id, descriptor) -> RegisterResult.Pass,
			callbacks -> (id, descriptor) -> {
				for (final var callback : callbacks)
					if (callback.blasterRegistered(id, descriptor) == RegisterResult.Cancel)
						return RegisterResult.Cancel;
				return RegisterResult.Pass;
			}
	);

	public static final Event<SpeciesRegistered> SPECIES_REGISTERED = EventFactory.createArrayBacked(
			SpeciesRegistered.class,
			(id, species) -> RegisterResult.Pass,
			callbacks -> (id, species) -> {
				for (final var callback : callbacks)
					if (callback.speciesRegistered(id, species) == RegisterResult.Cancel)
						return RegisterResult.Cancel;
				return RegisterResult.Pass;
			}
	);

	public static final Event<CollectHumanoidCustomization> COLLECT_HUMANOID_CUSTOMIZATION = EventFactory.createArrayBacked(CollectHumanoidCustomization.class, (id, species) -> {
	}, callbacks -> (id, options) -> {
		for (final var callback : callbacks)
			callback.collectHumanoidCustomization(id, options);
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
			if (LIGHTSABER_REGISTERED.invoker().lightsaberRegistered(entry.id, entry).shouldRegister())
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
			if (BLASTER_REGISTERED.invoker().blasterRegistered(entry.id, entry).shouldRegister())
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
			if (SPECIES_REGISTERED.invoker().speciesRegistered(entry.getSlug(), entry).shouldRegister())
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
		var humanoidCustomizationOptions = new HashMap<String, HumanoidCustomizationOptions>();
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
			COLLECT_HUMANOID_CUSTOMIZATION.invoker().collectHumanoidCustomization(option.getKey(), option.getValue());
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
