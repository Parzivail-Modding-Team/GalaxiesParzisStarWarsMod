package dev.pswg.item;

import dev.pswg.Galaxies;
import dev.pswg.registry.Registrar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ArmorItems
{
	public final Item helmet;
	public final Item chestplate;
	public final Item leggings;
	public final Item boots;

	public ArmorItems(String key, ArmorMaterial material, Item.Properties itemSettings)
	{
		helmet = Registrar.item(Galaxies.id(key + "_helmet"), Item::new, itemSettings.humanoidArmor(material, ArmorType.HELMET));
		chestplate = Registrar.item(Galaxies.id(key + "_chestplate"), Item::new, itemSettings.humanoidArmor(material, ArmorType.CHESTPLATE));
		leggings = Registrar.item(Galaxies.id(key + "_leggings"), Item::new, itemSettings.humanoidArmor(material, ArmorType.LEGGINGS));
		boots = Registrar.item(Galaxies.id(key + "_boots"), Item::new, itemSettings.humanoidArmor(material, ArmorType.BOOTS));
	}
}