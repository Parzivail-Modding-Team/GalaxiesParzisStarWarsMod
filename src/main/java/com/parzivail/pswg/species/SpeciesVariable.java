package com.parzivail.pswg.species;

import net.minecraft.util.Identifier;

public class SpeciesVariable
{
	private final Identifier name;
	private final String defaultValue;
	private final String[] possibleValues;

	public SpeciesVariable(Identifier name, String defaultValue, String... possibleValues)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		this.possibleValues = possibleValues;
	}

	public Identifier getName()
	{
		return name;
	}

	public String[] getPossibleValues()
	{
		return possibleValues;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}
}
