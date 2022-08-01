package com.parzivail.pswg.client.species;

import com.parzivail.util.client.LoreUtil;
import net.minecraft.util.Identifier;

public enum SwgSpeciesLore
{
	DESCRIPTION("description");

	private final String langKey;

	SwgSpeciesLore(String langKey)
	{
		this.langKey = langKey;
	}

	public String createLanguageKey(Identifier slug)
	{
		return LoreUtil.getLoreKey("species", new Identifier(slug.getNamespace(), slug.getPath() + "." + langKey));
	}
}
