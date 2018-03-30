package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.block.BlockBlasterWorkbench;
import com.parzivail.util.block.PBlock;
import com.parzivail.util.block.PBlockContainer;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by colby on 12/26/2017.
 */
public class BlockRegister
{
	public static PBlock labWall;
	public static PBlockContainer blasterWorkbench;

	public static void register()
	{
		register(labWall = new PBlock("labWall"));
		register(blasterWorkbench = new BlockBlasterWorkbench());
	}

	private static void register(PBlock item)
	{
		GameRegistry.registerBlock(item, item.name);
	}

	private static void register(PBlockContainer item)
	{
		GameRegistry.registerBlock(item, item.name);
		GameRegistry.registerTileEntity(item.createNewTileEntity(null, 0).getClass(), Resources.tileDot(item.name));
	}
}
