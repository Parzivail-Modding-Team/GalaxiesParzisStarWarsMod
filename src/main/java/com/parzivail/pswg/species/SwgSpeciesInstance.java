package com.parzivail.pswg.species;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.species.SwgSpeciesModel;
import com.parzivail.pswg.client.species.SwgSpeciesModels;
import com.parzivail.pswg.container.SwgSpecies;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

public class SwgSpeciesInstance
{
	public final Identifier species;
	public final String variant;

	public SwgSpeciesInstance(Identifier species, String variant)
	{
		this.species = species;
		this.variant = variant;
	}

	public SwgSpeciesInstance(Identifier species)
	{
		// uses default variant
		this(species, SwgSpecies.DEFAULT_VARIANT.get(species));
	}

	@Override
	public String toString()
	{
		return species.toString() + ";" + variant;
	}

	public SwgSpeciesModel getModel()
	{
		return SwgSpeciesModels.MODELS.get(species);
	}

	public Identifier getTexture()
	{
		//		Identifier base = Resources.identifier("textures/species/togruta_f/base_red.png");
		//		Identifier clothes = Resources.identifier("textures/species/global_f/clothes.png");
		//		Identifier montrals = Resources.identifier("textures/species/togruta_f/montrals_darkblue.png");
		//		Identifier eyes = Resources.identifier("textures/species/global_f/eyes.png");
		//		return Client.stackedTextureProvider.loadTexture(species.toString().replace(":", "_") + "/" + variant, base, clothes, montrals, eyes);
		return Resources.identifier("textures/species/" + species.getPath() + "/" + variant + ".png");
	}

	public static SwgSpeciesInstance fromString(String s)
	{
		if ("".equals(s) || !s.contains(";"))
			return null;

		String[] speciesParts = s.split(";", 2);
		Identifier species = new Identifier(speciesParts[0]);

		if (!SwgSpecies.SPECIES.contains(species))
			return null;

		String variant = speciesParts[1];

		if (!ArrayUtils.contains(SwgSpecies.VARIANTS.get(species), variant))
			return null;

		return new SwgSpeciesInstance(species, variant);
	}
}
