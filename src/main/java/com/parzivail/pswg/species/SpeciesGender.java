package com.parzivail.pswg.species;

import net.minecraft.util.Identifier;

public enum SpeciesGender
{
	MALE("m"),
	FEMALE("f");

	private static final String GENDER_SEPARATOR = "/";

	private final String slug;

	SpeciesGender(String slug)
	{
		this.slug = slug;
	}

	public String getSlug()
	{
		return slug;
	}

	public static Identifier stripGender(String genderedSlug)
	{
		String[] parts = genderedSlug.split(GENDER_SEPARATOR, 2);
		return new Identifier(parts[0]);
	}

	public static SpeciesGender fromModel(String genderedSlug)
	{
		String[] parts = genderedSlug.split(GENDER_SEPARATOR, 2);

		if (parts.length == 2 && parts[1].equals(FEMALE.slug))
			return FEMALE;

		return MALE;
	}

	public static Identifier toModel(SwgSpecies species)
	{
		Identifier slug = species.getSlug();
		SpeciesGender gender = species.getGender();
		return toModel(slug, gender);
	}

	public static Identifier toModel(Identifier species, SpeciesGender gender)
	{
		return new Identifier(species.getNamespace(), species.getPath() + GENDER_SEPARATOR + gender.slug);
	}
}
