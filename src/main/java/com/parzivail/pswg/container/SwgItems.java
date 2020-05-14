package com.parzivail.pswg.container;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.BlasterItem;

public class SwgItems
{
	public static class Blaster
	{
		public static final BlasterItem A280 = new BlasterItem(new BlasterItem.Settings().maxCount(1).damage(8).group(Galaxies.Tab));
	}
}
