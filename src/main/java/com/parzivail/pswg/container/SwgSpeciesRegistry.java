package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.species.SpeciesTogruta;
import com.parzivail.pswg.species.SwgSpecies;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.Function;

public class SwgSpeciesRegistry
{
	protected static final HashMap<Identifier, Function<String, SwgSpecies>> SPECIES = new HashMap<>();

	// Species with special meaning internally
	public static final Identifier SPECIES_GLOBAL = Resources.identifier("global"); // "global" species contains shared textures
	public static final Identifier SPECIES_NONE = new Identifier("none"); // "none" species delegates player models back to Minecraft

	// Normal species and variants
	public static final Identifier SPECIES_TOGRUTA = Resources.identifier("togruta");
	public static final Identifier SPECIES_TOGRUTA_M = Resources.identifier("togruta/m"); // TODO: automate m/f registration for models
	public static final Identifier SPECIES_TOGRUTA_F = Resources.identifier("togruta/f");

	static
	{
		SPECIES.put(SwgSpeciesRegistry.SPECIES_TOGRUTA, SpeciesTogruta::new);
	}

	public static SwgSpecies deserialize(String serialized)
	{
		Identifier id = SwgSpecies.getSpeciesSlug(serialized);
		if (!SPECIES.containsKey(id))
			return null;

		return SPECIES.get(id).apply(serialized);
	}
}
