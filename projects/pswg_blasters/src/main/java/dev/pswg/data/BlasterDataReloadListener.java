package dev.pswg.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.pswg.Blasters;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * A datapack loader for blaster definitions
 */
public class BlasterDataReloadListener implements SimpleSynchronousResourceReloadListener
{
	/**
	 * The instance of the listener
	 */
	public static final BlasterDataReloadListener INSTANCE = new BlasterDataReloadListener();

	/**
	 * The datapack folder in which this data resides
	 */
	public static final String FOLDER = "blasters";

	/**
	 * The set of blaster definitions currently associated with the loaded world
	 */
	private final HashMap<Identifier, BlasterDatapackDefinition> definitions = new HashMap<>();

	private BlasterDataReloadListener()
	{
	}

	/**
	 * Gets the current set of blaster definitions associated with the loaded
	 * world, keyed by the identifier deriving from their filename
	 */
	public HashMap<Identifier, BlasterDatapackDefinition> getDefinitions()
	{
		return definitions;
	}

	@Override
	public Identifier getFabricId()
	{
		return Blasters.id("data");
	}

	@Override
	public void reload(ResourceManager manager)
	{
		definitions.clear();

		for (var entry : manager.findResources(FOLDER, path -> path.getPath().endsWith(".json")).entrySet())
		{
			var key = entry.getKey();
			var resource = entry.getValue();

			try (
					var stream = resource.getInputStream();
					var reader = new InputStreamReader(stream)
			)
			{
				definitions.put(
						key.withPath(FilenameUtils.getBaseName(key.getPath())),
						BlasterDatapackDefinition.CODEC
								.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
								.result()
								.orElseThrow(() -> new IOException("Failed to decode blaster definition from JSON"))
				);
			}
			catch (Exception e)
			{
				Blasters.LOGGER.error("Failed to load data from blaster definition " + key, e);
			}
		}
	}
}
