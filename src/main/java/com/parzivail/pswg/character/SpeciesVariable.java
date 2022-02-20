package com.parzivail.pswg.character;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class SpeciesVariable
{
	public static final String NONE = "none";
	protected final Identifier sourceSpecies;
	protected final String name;

	public static boolean isNotEmpty(SwgSpecies species, SpeciesVariable variable)
	{
		return !species.getVariable(variable).equals(NONE);
	}

	public SpeciesVariable(Identifier sourceSpecies, String name)
	{
		this.sourceSpecies = sourceSpecies;
		this.name = name;
	}

	public abstract List<String> getPossibleValues();

	public abstract String getDefaultValue();

	public String getName()
	{
		return name;
	}

	public Identifier getSpeciesSlug()
	{
		return sourceSpecies;
	}

	public String getTranslationKey()
	{
		return SwgSpeciesRegistry.getTranslationKey(sourceSpecies) + "." + name;
	}

	public String getTranslationFor(String value)
	{
		return getTranslationKey() + "." + value;
	}
}
