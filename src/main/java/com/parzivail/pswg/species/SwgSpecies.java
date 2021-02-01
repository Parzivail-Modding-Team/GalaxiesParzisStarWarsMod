package com.parzivail.pswg.species;

import com.parzivail.pswg.Resources;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;

public abstract class SwgSpecies
{
	public abstract Identifier getModel();

	public abstract Identifier getSlug();

	public abstract SpeciesGender getGender();

	public abstract Identifier serialize();

	public abstract void deserialize(Identifier identifier);

	public abstract Map<Identifier, String> getDefaultVariables();

	public abstract SpeciesVariable[] getVariables();

	@Environment(EnvType.CLIENT)
	public abstract Collection<Identifier> getTextureStack();

	protected static Identifier getTexture(SwgSpecies species, String texture)
	{
		return getTexture(species.getSlug(), texture);
	}

	protected static Identifier getGenderedTexture(SwgSpecies species, String texture)
	{
		return getTexture(SpeciesGender.toSlug(species), texture);
	}

	protected static Identifier getGlobalTexture(String texture)
	{
		return getTexture(Resources.identifier("global"), texture);
	}

	protected static Identifier getGenderedGlobalTexture(SpeciesGender gender, String texture)
	{
		return getTexture(Resources.identifier("global_" + gender.getSlug()), texture);
	}

	private static Identifier getTexture(Identifier slug, String texture)
	{
		return new Identifier(slug.getNamespace(), "textures/species/" + slug.getPath() + "/" + texture + ".png");
	}
}
