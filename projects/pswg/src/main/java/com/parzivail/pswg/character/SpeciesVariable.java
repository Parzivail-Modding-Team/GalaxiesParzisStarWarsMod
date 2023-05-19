package com.parzivail.pswg.character;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class SpeciesVariable
{
	public static final String NONE = "none";
	protected final Identifier targetSpecies;
	protected final String name;

	public static boolean isNotNone(SwgSpecies species, SpeciesVariable variable)
	{
		return !species.getVariable(variable).equals(NONE);
	}

	public SpeciesVariable(Identifier targetSpecies, String name)
	{
		this.targetSpecies = targetSpecies;
		this.name = name;
	}

	public abstract List<String> getPossibleValues();

	public abstract String getDefaultValue();

	public String getName()
	{
		return name;
	}

	public Identifier getTargetSpeciesSlug()
	{
		return targetSpecies;
	}

	public Identifier getDefiningSpeciesSlug()
	{
		return targetSpecies;
	}

	public String getTranslationKey()
	{
		return SwgSpeciesRegistry.getTranslationKey(getDefiningSpeciesSlug()) + "." + name;
	}

	public String getTranslationFor(String value)
	{
		return getTranslationKey() + "." + value;
	}
}
