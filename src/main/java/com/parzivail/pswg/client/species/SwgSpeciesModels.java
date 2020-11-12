package com.parzivail.pswg.client.species;

import com.parzivail.pswg.client.model.npc.*;
import com.parzivail.pswg.container.SwgSpecies;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SwgSpeciesModels
{
	public static final HashMap<Identifier, SwgSpeciesModel> MODELS = new HashMap<>();

	static
	{
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_M, new ModelTogrutaM<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_F, new ModelTogrutaF<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_CHAGRIAN_M, new ModelChagrianCombined<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_CHAGRIAN_F, new ModelChagrianCombined<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TWILEK_M, new ModelTwilekCombined<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TWILEK_F, new ModelTwilekCombined<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BITH_M, new ModelBithCombined<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BITH_F, new ModelBithCombined<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_JAWA, new ModelJawa<>(0)));
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier, model);
	}
}
