package com.parzivail.pswg.species;

import net.minecraft.util.Identifier;

public class SpeciesColorVariable extends SpeciesVariable
{
	public SpeciesColorVariable(Identifier parent, String name, int defaultValue)
	{
		super(parent, name, getHexString(defaultValue));
	}

	private static String getHexString(int color)
	{
		return String.format("%06x", color & 0xFFFFFF);
	}

	@Override
	public String getTranslationFor(String value)
	{
		return getTranslationKey();
	}
}
