package com.parzivail.pswg.container.data;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;

public class SwgBlasterLoader
{
	public static void load()
	{
		FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
			try
			{
				Files.walk(modContainer.getPath("assets")).forEach(path -> {
					if (path.toString().matches(".*lang.[a-z_]+(\\.json)$"))
					{
					}
				});
			}
			catch (IOException e)
			{
			}
		});
	}
}
