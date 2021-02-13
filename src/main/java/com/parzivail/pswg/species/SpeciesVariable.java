package com.parzivail.pswg.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.util.Identifier;

public class SpeciesVariable
{
	private final Identifier species;
	private final String name;
	private final String defaultValue;
	private final String[] possibleValues;

	public SpeciesVariable(Identifier parent, String name, String defaultValue, String... possibleValues)
	{
		this.species = parent;
		this.name = name;
		this.defaultValue = defaultValue;
		this.possibleValues = possibleValues;
	}

	public String getName()
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

	public String getTranslationKey()
	{
		return SwgSpeciesRegistry.getTranslationKey(species) + "." + name;
	}

	public String getTranslationFor(String value)
	{
		return getTranslationKey() + "." + value;
	}
}
