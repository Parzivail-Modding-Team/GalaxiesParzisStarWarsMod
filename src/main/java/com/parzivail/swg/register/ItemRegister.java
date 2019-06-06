package com.parzivail.swg.register;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.ItemBlaster;
import com.parzivail.swg.item.ItemLightsaber;
import com.parzivail.swg.item.data.BlasterDescriptor;
import com.parzivail.swg.register.deserializer.ModuleBlasterDeserializer;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class ItemRegister
{
	public static ItemBlaster blaster;
	public static ItemLightsaber lightsaber;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		//		r.register(blaster = new ItemBlaster(new BlasterDescriptor("a280", 8, 0, 30, 0, 0, 10, 0xFF0000, 10, 0, 3)));
		r.register(lightsaber = new ItemLightsaber());

		loadModularBlasters(r);

		StarWarsGalaxy.proxy.onRegisterItem(event);
	}

	private static void loadModularBlasters(IForgeRegistry<Item> r)
	{
		Gson gson = new GsonBuilder().registerTypeAdapter(BlasterDescriptor.class, new ModuleBlasterDeserializer()).create();

		File[] resourceFiles = getResourceFolderFiles("assets/" + Resources.MODID + "/modules/blasters");

		for (File f : resourceFiles)
		{
			try
			{
				BlasterDescriptor d = gson.fromJson(new FileReader(f), BlasterDescriptor.class);
				r.register(blaster = new ItemBlaster(d));
			}
			catch (Exception e)
			{
				Lumberjack.err("Failed to load blaster module: " + f.getName());
				e.printStackTrace();
			}
		}
	}

	private static File[] getResourceFolderFiles(String folder)
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(folder);
		String path = url.getPath();
		return new File(path).listFiles();
	}
}
