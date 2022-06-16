package com.parzivail.util.client;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TooltipUtil
{
	public static Text getLore(Item item, Object... args)
	{
		var reg = Registry.ITEM.getId(item);
		return Text.translatable(getLoreKey(reg), args).formatted(Formatting.GRAY, Formatting.ITALIC);
	}

	public static String getLoreKey(Item item)
	{
		var reg = Registry.ITEM.getId(item);
		return getLoreKey(reg);
	}

	private static String getLoreKey(Identifier registryId)
	{
		return "tooltip." + registryId.getNamespace() + ".lore." + registryId.getPath();
	}

	public static Text getStatus(Item item, Object... args)
	{
		var reg = Registry.ITEM.getId(item);
		return Text.translatable(getStatusKey(reg), args).formatted(Formatting.GRAY);
	}

	public static String getStatusKey(Item item)
	{
		var reg = Registry.ITEM.getId(item);
		return getStatusKey(reg);
	}

	private static String getStatusKey(Identifier registryId)
	{
		return "tooltip." + registryId.getNamespace() + ".status." + registryId.getPath();
	}
}
