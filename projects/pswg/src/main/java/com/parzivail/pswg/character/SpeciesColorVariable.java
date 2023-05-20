package com.parzivail.pswg.character;

import com.parzivail.util.math.ColorUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SpeciesColorVariable extends SpeciesVariable
{
	private final int defaultColor;
	private final ArrayList<String> allowedValues = new ArrayList<>();

	public SpeciesColorVariable(Identifier parent, String name, int defaultColor)
	{
		this(parent, name, defaultColor, new int[0]);
	}

	public SpeciesColorVariable(Identifier parent, String name, int defaultColor, int[] allowedValues)
	{
		super(parent, name);
		this.defaultColor = defaultColor;

		for (var value : allowedValues)
			this.allowedValues.add(Integer.toHexString(value));
	}

	@Override
	public List<String> getPossibleValues()
	{
		return allowedValues;
	}

	@Override
	public String getDefaultValue()
	{
		return ColorUtil.toResourceId(defaultColor);
	}

	@Override
	public String getTranslationFor(String value)
	{
		if (getPossibleValues().isEmpty())
			return getTranslationKey();
		else
			return getTranslationKey() + ".preset." + value;
	}
}
