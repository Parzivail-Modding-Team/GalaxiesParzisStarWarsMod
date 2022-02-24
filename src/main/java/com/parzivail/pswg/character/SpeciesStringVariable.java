package com.parzivail.pswg.character;

import net.minecraft.util.Identifier;

import java.util.List;

public class SpeciesStringVariable extends SpeciesVariable
{
	private final String defaultValue;
	private final List<String> possibleValues;

	public SpeciesStringVariable(Identifier parent, String name, String defaultValue, String... possibleValues)
	{
		super(parent, name);
		this.defaultValue = defaultValue;
		this.possibleValues = List.of(possibleValues);
	}

	@Override
	public List<String> getPossibleValues()
	{
		return possibleValues;
	}

	@Override
	public String getDefaultValue()
	{
		return defaultValue;
	}
}
