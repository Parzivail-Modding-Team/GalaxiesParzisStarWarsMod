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
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_AQUALISH_M, new ModelAqualish<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_AQUALISH_F, new ModelAqualish<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BITH_M, new ModelBith<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BITH_F, new ModelBith<>(false, 0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BOTHAN_M, new ModelBothan<>(true, 0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BOTHAN_F, new ModelBothan<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_CHAGRIAN_M, new ModelChagrian<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_CHAGRIAN_F, new ModelChagrian<>(false, 0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_KAMINOAN_M, new ModelKaminoan<>(true, 0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_KAMINOAN_F, new ModelKaminoan<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_M, new ModelTogrutaM<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_F, new ModelTogrutaF<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_JAWA, new ModelJawa<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TWILEK_M, new ModelTwilek<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TWILEK_F, new ModelTwilek<>(false, 0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_WOOKIEE_M, new ModelWookiee<>(true, 0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_WOOKIEE_F, new ModelWookiee<>(false, 0)));
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier, model);
	}
}
