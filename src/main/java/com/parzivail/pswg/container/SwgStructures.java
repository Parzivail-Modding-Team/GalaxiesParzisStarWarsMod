package com.parzivail.pswg.container;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.util.scarif.ScarifStructure;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class SwgStructures
{
	public static void cleanUpTemporaryFiles()
	{
		var modDir = FabricLoader.getInstance().getGameDir().resolve("mods");
		try
		{
			for (var file : (Iterable<Path>)Files.list(modDir)::iterator)
			{
				var name = file.getFileName().toString();
				if (name.startsWith("zipfstmp") && name.endsWith(".tmp"))
				{
					try
					{
						Files.delete(file);
					}
					catch (IOException ignored)
					{
						// Ignore this. If we get an exception here, we're on Windows,
						// and the file is being used.
					}
				}
			}
		}
		catch (IOException ignored)
		{
		}
	}

	public static class General
	{
		public static final Supplier<ScarifStructure> Region = Suppliers.memoize(() -> ScarifStructure.read(Resources.id("structures/entire_region.scrf2")));

		public static void register()
		{
			// no-op to make sure the class is loaded
		}
	}
}
