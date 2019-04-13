package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.model.ModelLocationInformation;
import com.parzivail.swg.model.PModelLoader;
import com.parzivail.swg.model.PModelVariantLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SwgClientProxy extends SwgProxy
{
	public static Minecraft mc;

	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		mc = Minecraft.getMinecraft();
		ModelLoaderRegistry.registerLoader(PModelLoader.INSTANCE);
		ModelLoaderRegistry.registerLoader(PModelVariantLoader.INSTANCE);
	}

	@Override
	public void init(FMLInitializationEvent e)
	{
		ModelLocationInformation.init(mc.getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper());
	}

	@Override
	public void registerItemRenderer(Item item, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Resources.modColon(id), "inventory"));
	}
}
