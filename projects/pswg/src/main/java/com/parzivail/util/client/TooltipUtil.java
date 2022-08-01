package com.parzivail.util.client;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TooltipUtil
{
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
