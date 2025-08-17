package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.registry.Registrar;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

public class ArmorItems
{
	public final ArmorItem helmet;
	public final ArmorItem chestplate;
	public final ArmorItem leggings;
	public final ArmorItem boots;

	public ArmorItems(String key, ArmorMaterial material, Item.Settings itemSettings)
	{
		helmet = Registrar.item(Gadgets.id(key + "_helmet"), settings -> new ArmorItem(material, EquipmentType.HELMET, settings), itemSettings);
		chestplate = Registrar.item(Gadgets.id(key + "_chestplate"), settings -> new ArmorItem(material, EquipmentType.CHESTPLATE, settings), itemSettings);
		leggings = Registrar.item(Gadgets.id(key + "_leggings"), settings -> new ArmorItem(material, EquipmentType.LEGGINGS, settings), itemSettings);
		boots = Registrar.item(Gadgets.id(key + "_boots"), settings -> new ArmorItem(material, EquipmentType.BOOTS, settings), itemSettings);
	}
}