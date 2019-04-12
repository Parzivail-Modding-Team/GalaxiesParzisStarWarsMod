package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SwgClientProxy extends SwgProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		//		ModelLoaderRegistry.registerLoader(PModelLoader.INSTANCE);
	}

	@Override
	public void registerItemRenderer(Item item, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Resources.modColon(id), "inventory"));
	}
}
