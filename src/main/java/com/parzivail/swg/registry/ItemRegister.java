package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.ItemBlasterSmallGasCanister;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.weapon.ItemBlasterRifle;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by colby on 12/26/2017.
 */
public class ItemRegister
{
	public static PItem rifleE11;

	public static PItem powerPackSmallGasCanister;

	public static void register()
	{
		register(rifleE11 = new ItemBlasterRifle("e11", 3, 1, 100, 0xFF0000));
		register(powerPackSmallGasCanister = new ItemBlasterSmallGasCanister());
	}

	private static void register(PItem item)
	{
		GameRegistry.registerItem(item, item.name, Resources.MODID);
	}
}
