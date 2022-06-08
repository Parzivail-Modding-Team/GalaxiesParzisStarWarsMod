package com.parzivail.pswg.character;

import com.parzivail.pswg.data.SwgSpeciesManager;
import net.minecraft.util.Identifier;

import java.util.List;

public class DatapackedSpeciesColorVariable extends SpeciesColorVariable
{
	public DatapackedSpeciesColorVariable(Identifier sourceSpecies, String name)
	{
		super(sourceSpecies, name, 0xFFFFFF);
	}

	@Override
	public List<String> getPossibleValues()
	{
		var desc = SwgSpeciesManager.INSTANCE.getDataAndAssert(targetSpecies);
		return desc.variables.get(name).possibleValues;
	}

	@Override
	public String getDefaultValue()
	{
		var desc = SwgSpeciesManager.INSTANCE.getDataAndAssert(targetSpecies);
		return desc.variables.get(name).defaultValue;
	}

	@Override
	public Identifier getDefiningSpeciesSlug()
	{
		var desc = SwgSpeciesManager.INSTANCE.getDataAndAssert(targetSpecies);
		var variable = desc.variables.get(name);

		if (variable.definingSpecies != null)
			return variable.definingSpecies;

		return super.getDefiningSpeciesSlug();
	}

	@Override
	public String getTranslationFor(String value)
	{
		return getTranslationKey() + ".preset." + value;
	}
}
