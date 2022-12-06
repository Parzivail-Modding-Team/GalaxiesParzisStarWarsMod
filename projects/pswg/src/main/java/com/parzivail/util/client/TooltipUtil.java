package com.parzivail.util.client;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TooltipUtil
{
	public static Text getStatus(Item item, Object... args)
	{
		var reg = Registries.ITEM.getId(item);
		return Text.translatable(getStatusKey(reg), args).formatted(Formatting.GRAY);
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
}
