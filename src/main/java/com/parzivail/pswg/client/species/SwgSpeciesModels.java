package com.parzivail.pswg.client.species;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.model.npc.ModelTogrutaF;
import com.parzivail.pswg.client.model.npc.ModelTogrutaM;
import com.parzivail.pswg.container.SwgSpecies;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SwgSpeciesModels
{
	public static final HashMap<Identifier, SwgSpeciesModel> MODELS = new HashMap<>();

	static
	{
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_M, Resources.identifier("textures/species/togruta_m.png"), new ModelTogrutaM<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_F, Resources.identifier("textures/species/togruta_f.png"), new ModelTogrutaF<>(0)));
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier, model);
	}
}
