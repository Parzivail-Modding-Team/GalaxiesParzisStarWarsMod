package com.parzivail.util.registry;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public class ArmorItems
{
	public final ArmorItem helmet;
	public final ArmorItem chestplate;
	public final ArmorItem leggings;
	public final ArmorItem boots;

	public ArmorItems(RegistryEntry<ArmorMaterial> material, Item.Settings settings)
	{
		helmet = new ArmorItem(material, ArmorItem.Type.HELMET, settings);
		chestplate = new ArmorItem(material, ArmorItem.Type.CHESTPLATE, settings);
		leggings = new ArmorItem(material, ArmorItem.Type.LEGGINGS, settings);
		boots = new ArmorItem(material, ArmorItem.Type.BOOTS, settings);
	}
}
