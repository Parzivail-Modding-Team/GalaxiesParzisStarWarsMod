package com.parzivail.pswg.character;

import com.google.common.collect.ImmutableList;
import com.parzivail.pswg.api.HumanoidCustomizationOptions;
import net.minecraft.util.Identifier;

import java.util.List;

public class BakedSpeciesColorVariable extends SpeciesColorVariable
{
	private String defaultValue;
	private List<String> possibleValues;

	public BakedSpeciesColorVariable(Identifier sourceSpecies, String name)
	{
		super(sourceSpecies, name, 0xFFFFFF);
	}

	public void bakeWith(HumanoidCustomizationOptions options)
	{
		if (this.defaultValue != null || this.possibleValues != null)
			throw new RuntimeException("Cannot re-bake species variable");

		this.defaultValue = options.defaultValue;
		this.possibleValues = ImmutableList.copyOf(options.possibleValues);
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
