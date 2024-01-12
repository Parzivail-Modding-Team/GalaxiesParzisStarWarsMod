package com.parzivail.datagen;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class AssetUtils
{
	public static Identifier getRegistryName(ItemConvertible item)
	{
		return Registries.ITEM.getId(item.asItem());
	}

	public static Identifier getRegistryName(Object data)
	{
		if (data instanceof Block block)
			return getRegistryName(block);

		if (data instanceof Item item)
			return getRegistryName(item);

		if (data instanceof ItemConvertible item)
			return getRegistryName(item);

		throw new RuntimeException("No registry getter for " + data);
	}

	public static Identifier getRegistryName(Block block)
	{
		return Registries.BLOCK.getId(block);
	}

	public static Identifier getTextureName(Block block)
	{
		var id = Registries.BLOCK.getId(block);
		return new Identifier(id.getNamespace(), "block/" + id.getPath());
	}

	public static Identifier getTextureName(Item item)
	{
		var id = Registries.ITEM.getId(item);
		return new Identifier(id.getNamespace(), "item/" + id.getPath());
	}
}
