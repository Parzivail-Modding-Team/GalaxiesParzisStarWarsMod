package com.parzivail.pswg.character;

import com.parzivail.pswg.data.SwgSpeciesManager;
import net.minecraft.util.Identifier;

import java.util.List;

public class DatapackedSpeciesVariable extends SpeciesVariable
{
	public DatapackedSpeciesVariable(Identifier sourceSpecies, String name)
	{
		super(sourceSpecies, name);
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
}
