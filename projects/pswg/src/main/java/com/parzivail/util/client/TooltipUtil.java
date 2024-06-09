package com.parzivail.util.client;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TooltipUtil
{
	public static MutableText note(MutableText text)
	{
		return text.formatted(Formatting.GRAY);
	}

	public static MutableText aside(MutableText text)
	{
		return text.formatted(Formatting.GRAY, Formatting.ITALIC);
	}

	public static Text getStatus(Item item, Object... args)
	{
		var reg = Registries.ITEM.getId(item);
		return note(Text.translatable(getStatusKey(reg), args));
	}

	public static String getStatusKey(Item item)
	{
		var reg = Registries.ITEM.getId(item);
		return getStatusKey(reg);
	}

	private static String getStatusKey(Identifier registryId)
	{
		return "tooltip." + registryId.getNamespace() + ".status." + registryId.getPath();
	}

	public static Text getLore(Item item, Object... args)
	{
		return aside(Text.translatable(getItemLoreKey(item), args));
	}

	public static String getItemLoreKey(Item item)
	{
		var reg = Registries.ITEM.getId(item);
		return reg.toTranslationKey("lore.item");
	}
}
