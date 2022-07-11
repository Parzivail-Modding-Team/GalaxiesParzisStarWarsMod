package com.parzivail.pswg.character;

import com.parzivail.util.client.ColorUtil;
import net.minecraft.util.Identifier;

import java.util.List;

public class SpeciesColorVariable extends SpeciesVariable
{
	private final int defaultColor;

	public SpeciesColorVariable(Identifier parent, String name, int defaultColor)
	{
		super(parent, name);
		this.defaultColor = defaultColor;
	}

	@Override
	public List<String> getPossibleValues()
	{
		return List.of();
	}

	@Override
	public String getDefaultValue()
	{
		return ColorUtil.toResourceId(defaultColor);
	}

	@Override
	public String getTranslationFor(String value)
	{
		return getTranslationKey();
	}
}
