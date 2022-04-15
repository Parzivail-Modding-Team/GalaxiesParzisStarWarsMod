package com.parzivail.util.client;

import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LoreUtil
{
	public static TranslatableText getLore(Item item)
	{
		var reg = Registry.ITEM.getId(item);
		return getLore(reg);
	}

	private static TranslatableText getLore(Identifier registryId)
	{
		return new TranslatableText("tooltip." + registryId.getNamespace() + ".lore." + registryId.getPath());
	}
}
