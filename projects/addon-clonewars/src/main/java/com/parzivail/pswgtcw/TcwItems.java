package com.parzivail.pswgtcw;

import com.parzivail.pswg.Galaxies;
import com.parzivail.util.registry.ArmorItems;
import com.parzivail.util.registry.RegistryName;
import com.parzivail.util.registry.RegistryOrder;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;

public class TcwItems
{
	@RegistryOrder(0)
	public static class Armor
	{
		@RegistryName("phase1_clonetrooper")
		public static final ArmorItems Phase1Clone = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1).group(Galaxies.TabItems));
	}
}
