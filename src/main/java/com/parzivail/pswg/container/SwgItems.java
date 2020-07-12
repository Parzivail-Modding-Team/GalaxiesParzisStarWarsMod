package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.BlasterItem;
import com.parzivail.pswg.item.LightsaberItem;
import net.minecraft.item.Item;

public class SwgItems
{
	public static class Blaster
	{
		public static final BlasterItem A280 = new BlasterItem(new BlasterItem.Settings().maxCount(1).damage(8).group(Galaxies.Tab));
	}

	public static final LightsaberItem Lightsaber = new LightsaberItem(new Item.Settings().maxCount(1).group(Galaxies.Tab));
}
