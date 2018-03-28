package com.parzivail.swg.registry;

import com.parzivail.swg.block.PBlock;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by colby on 12/26/2017.
 */
public class BlockRegister
{
	public static PBlock labWall;

	public static void register()
	{
		register(labWall = new PBlock("labWall"));
	}

	private static void register(PBlock item)
	{
		GameRegistry.registerBlock(item, item.name);
	}
}
