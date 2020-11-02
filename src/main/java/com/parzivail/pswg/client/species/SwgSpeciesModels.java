package com.parzivail.pswg.client.species;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.model.npc.ModelBithCombined;
import com.parzivail.pswg.client.model.npc.ModelTogrutaF;
import com.parzivail.pswg.client.model.npc.ModelTogrutaM;
import com.parzivail.pswg.client.model.npc.ModelTwilekCombined;
import com.parzivail.pswg.container.SwgSpecies;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SwgSpeciesModels
{
	public static final HashMap<Identifier, SwgSpeciesModel> MODELS = new HashMap<>();

	static
	{
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_M, Resources.identifier("textures/species/togruta/male/yellow.png"), new ModelTogrutaM<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TOGRUTA_F, Resources.identifier("textures/species/togruta/female/yellow.png"), new ModelTogrutaF<>(0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_CHAGRIAN_M, Resources.identifier("textures/species/chagrian_m.png"), new ModelChagrianM<>(0)));
		//		register(new SwgSpeciesModel(SwgSpecies.SPECIES_CHAGRIAN_F, Resources.identifier("textures/species/chagrian_f.png"), new ModelChagrianF<>(0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TWILEK_M, Resources.identifier("textures/species/twilek/male/green.png"), new ModelTwilekCombined<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_TWILEK_F, Resources.identifier("textures/species/twilek/female/green.png"), new ModelTwilekCombined<>(false, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BITH_M, Resources.identifier("textures/species/bith/male/pink.png"), new ModelBithCombined<>(true, 0)));
		register(new SwgSpeciesModel(SwgSpecies.SPECIES_BITH_F, Resources.identifier("textures/species/bith/female/pink.png"), new ModelBithCombined<>(false, 0)));
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier, model);
	}
}
