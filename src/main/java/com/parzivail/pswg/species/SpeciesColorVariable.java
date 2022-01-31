package com.parzivail.pswg.species;

import com.parzivail.util.client.ColorUtil;
import net.minecraft.util.Identifier;

public class SpeciesColorVariable extends SpeciesVariable
{
	public SpeciesColorVariable(Identifier parent, String name, int defaultValue)
	{
		super(parent, name, ColorUtil.toResourceId(defaultValue));
	}

	@Override
	public String getTranslationFor(String value)
	{
		return getTranslationKey();
	}
}
