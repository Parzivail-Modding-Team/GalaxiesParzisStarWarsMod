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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ItemRegister
{
	public static ItemBlaster blaster;
	public static ItemLightsaber lightsaber;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(lightsaber = new ItemLightsaber());

		loadModularBlasters(r);

		StarWarsGalaxy.proxy.onRegisterItem(event);
	}

	private static void loadModularBlasters(IForgeRegistry<Item> r)
	{
		Gson gson = new GsonBuilder().registerTypeAdapter(BlasterDescriptor.class, new ModuleBlasterDeserializer()).create();

		Path[] resourceFiles = getResourceFolderFiles("assets/" + Resources.MODID + "/modules/blasters");

		for (Path f : resourceFiles)
		{
			try (InputStream inputStream = Files.newInputStream(f))
			{
				try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream))
				{
					BlasterDescriptor d = gson.fromJson(inputStreamReader, BlasterDescriptor.class);
					r.register(blaster = new ItemBlaster(d));
				}
			}
			catch (Exception e)
			{
				Lumberjack.err("Failed to load blaster module: " + f.getFileName());
				e.printStackTrace();
			}
		}
	}

	private static Path[] getResourceFolderFiles(String folder)
	{
		ClassLoader loader = ItemRegister.class.getClassLoader();
		URL url = loader.getResource(folder);
		try
		{
			Path path = getPath(url.toURI());
			return Files.list(path).toArray(Path[]::new);
		}
		catch (URISyntaxException | IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static Path getPath(URI uri) throws IOException
	{
		try
		{
			return Paths.get(uri);
		}
		catch (FileSystemNotFoundException e)
		{
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			FileSystem zipfs = FileSystems.newFileSystem(uri, env);

			return Paths.get(uri);
		}
	}
}
