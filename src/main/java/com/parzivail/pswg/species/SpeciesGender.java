package com.parzivail.pswg.species;

import net.minecraft.util.Identifier;

public enum SpeciesGender
{
	MALE("m"),
	FEMALE("f");

	private final String slug;

	SpeciesGender(String slug)
	{
		this.slug = slug;
	}

	public String getSlug()
	{
		return slug;
	}

	public static SpeciesGender fromSlug(String genderedSlug)
	{
		String[] parts = genderedSlug.split("_", 2);

		if (parts.length == 2 && parts[1].equals(FEMALE.slug))
			return FEMALE;

		return MALE;
	}

	public static Identifier toSlug(SwgSpecies species)
	{
		Identifier slug = species.getSlug();
		SpeciesGender gender = species.getGender();
		return new Identifier(slug.getNamespace(), slug.getPath() + '_' + gender.slug);
	}
}
