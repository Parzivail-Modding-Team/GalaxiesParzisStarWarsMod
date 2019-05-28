package com.parzivail.swg.register;

import com.parzivail.swg.item.ItemBlasterBase;
import com.parzivail.swg.item.data.BlasterDescriptor;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemRegister
{
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(new ItemBlasterBase(new BlasterDescriptor("a280", 8, 0, 30, 0, 0, 10, 0xFF0000, 10, 0, 3)));
	}
}
