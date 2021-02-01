package com.parzivail.pswg.species;

import net.minecraft.util.Identifier;

public class SpeciesVariable
{
	private final Identifier name;
	private final String[] possibleValues;

	public SpeciesVariable(Identifier name, String... possibleValues)
	{
		this.name = name;
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
}
