package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.registry.Registrar;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

public class ArmorItems
{
	public final Item helmet;
	public final Item chestplate;
	public final Item leggings;
	public final Item boots;

	public ArmorItems(String key, ArmorMaterial material, Item.Settings itemSettings)
	{
		helmet = Registrar.item(Gadgets.id(key + "_helmet"), Item::new, itemSettings.armor(material, EquipmentType.HELMET));
		chestplate = Registrar.item(Gadgets.id(key + "_chestplate"), Item::new, itemSettings.armor(material, EquipmentType.CHESTPLATE));
		leggings = Registrar.item(Gadgets.id(key + "_leggings"), Item::new, itemSettings.armor(material, EquipmentType.LEGGINGS));
		boots = Registrar.item(Gadgets.id(key + "_boots"), Item::new, itemSettings.armor(material, EquipmentType.BOOTS));
	}
}