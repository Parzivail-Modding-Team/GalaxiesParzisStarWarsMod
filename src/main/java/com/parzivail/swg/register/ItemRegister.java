package com.parzivail.swg.register;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ItemBlaster;
import com.parzivail.swg.item.ItemLightsaber;
import com.parzivail.swg.item.data.BlasterDescriptor;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemRegister
{
	public static ItemBlaster blaster;
	public static ItemLightsaber lightsaber;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(blaster = new ItemBlaster(new BlasterDescriptor("a280", 8, 0, 30, 0, 0, 10, 0xFF0000, 10, 0, 3)));
		r.register(lightsaber = new ItemLightsaber());

		StarWarsGalaxy.proxy.onRegisterItem(event);
	}
}
