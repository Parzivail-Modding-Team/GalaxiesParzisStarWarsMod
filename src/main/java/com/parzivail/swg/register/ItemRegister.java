package com.parzivail.swg.register;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ItemBlaster;
import com.parzivail.swg.item.ItemLightsaber;
import com.parzivail.swg.item.data.BlasterDescriptor;
import com.parzivail.util.item.ModularItems;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemRegister
{
	public static ItemLightsaber lightsaber;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(lightsaber = new ItemLightsaber());

		for (BlasterDescriptor d : ModularItems.getBlasterDescriptors())
			r.register(new ItemBlaster(d));

		StarWarsGalaxy.proxy.onRegisterItem(event);
	}
}
