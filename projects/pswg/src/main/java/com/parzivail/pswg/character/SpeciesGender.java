package com.parzivail.pswg.character;

import net.minecraft.util.Identifier;

public enum SpeciesGender
{
	MALE("m"),
	FEMALE("f");

	public static final String GENDER_SEPARATOR = "/";

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
		var parts = genderedSlug.split(GENDER_SEPARATOR, 2);
		return new Identifier(parts[0]);
	}
}
