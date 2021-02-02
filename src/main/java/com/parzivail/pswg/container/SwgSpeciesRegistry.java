package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.species.SwgSpecies;
import com.parzivail.pswg.species.species.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SwgSpeciesRegistry
{
	protected static final HashMap<Identifier, Function<String, SwgSpecies>> SPECIES = new HashMap<>();

	// Species with special meaning internally
	public static final Identifier SPECIES_GLOBAL = Resources.identifier("global"); // "global" species contains shared textures
	public static final Identifier SPECIES_NONE = new Identifier("none"); // "none" species delegates player models back to Minecraft

	// Normal species and variants
	public static final Identifier SPECIES_AQUALISH = Resources.identifier("aqualish");
	public static final Identifier SPECIES_BITH = Resources.identifier("bith");
	public static final Identifier SPECIES_BOTHAN = Resources.identifier("bothan");
	public static final Identifier SPECIES_CHAGRIAN = Resources.identifier("chagrian");
	public static final Identifier SPECIES_JAWA = Resources.identifier("jawa");
	public static final Identifier SPECIES_KAMINOAN = Resources.identifier("kaminoan");
	public static final Identifier SPECIES_TOGRUTA = Resources.identifier("togruta");
	public static final Identifier SPECIES_TWILEK = Resources.identifier("twilek");

	static
	{
		SPECIES.put(SwgSpeciesRegistry.SPECIES_AQUALISH, SpeciesAqualish::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_BITH, SpeciesBith::new);
//		SPECIES.put(SwgSpeciesRegistry.SPECIES_BOTHAN, SpeciesBothan::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_CHAGRIAN, SpeciesChagrian::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_JAWA, SpeciesJawa::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_KAMINOAN, SpeciesKaminoan::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_TOGRUTA, SpeciesTogruta::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_TWILEK, SpeciesTwilek::new);
	}

	public static List<SwgSpecies> getSpecies()
	{
		return SPECIES
				.values()
				.stream()
				.map(stringSwgSpeciesFunction -> stringSwgSpeciesFunction.apply(null))
				.collect(Collectors.toList());
	}

	public static SwgSpecies deserialize(String serialized)
	{
		Identifier id = SwgSpecies.getSpeciesSlug(serialized);
		if (!SPECIES.containsKey(id))
			return null;

		return SPECIES.get(id).apply(serialized);
	}
}
