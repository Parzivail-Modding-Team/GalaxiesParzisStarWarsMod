package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.weapon.ItemSlugRifle;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by colby on 12/26/2017.
 */
public class ItemRegister
{
	public static PItem slugRifle;

	public static void register()
	{
		GameRegistry.registerItem(slugRifle = new ItemSlugRifle(), slugRifle.name, Resources.MODID);
	}
}
