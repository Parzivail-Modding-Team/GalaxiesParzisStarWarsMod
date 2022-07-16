package com.parzivail.util.registry;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;

public class ArmorItems
{
	public final ArmorItem helmet;
	public final ArmorItem chestplate;
	public final ArmorItem leggings;
	public final ArmorItem boots;

	public ArmorItems(ArmorMaterial material, Item.Settings settings)
	{
		helmet = new ArmorItem(material, EquipmentSlot.HEAD, settings);
		chestplate = new ArmorItem(material, EquipmentSlot.CHEST, settings);
		leggings = new ArmorItem(material, EquipmentSlot.LEGS, settings);
		boots = new ArmorItem(material, EquipmentSlot.FEET, settings);
	}
}
