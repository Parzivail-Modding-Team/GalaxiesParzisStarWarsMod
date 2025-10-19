package dev.pswg.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.pswg.Galaxies;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * A datapack loader for codec-backed data
 */
public class CodecDataLoader<T> implements SynchronousResourceReloader
{
	/**
	 * The logger used while loading data
	 */
	private final Logger logger;

	/**
	 * The set of data definitions currently associated with the loaded world
	 */
	private final HashMap<Identifier, T> definitions = new HashMap<>();

	/**
	 * The id of the logger
	 */
	private final Identifier id;

	/**
	 * The path of the folder from which data will be loaded
	 */
	private final String folderName;

	/**
	 * A filter that will be used to select files from within the specified folder
	 */
	private final Predicate<Identifier> filter;

	/**
	 * The codec that will be used to decode the given type from the
	 */
	private final Codec<? extends T> codec;

	/**
	 * Creates a new codec-backed data loader
	 *
	 * @param id         The identifier for this data loader instance.
	 * @param folderName The path of the folder from which data will be loaded.
	 * @param filter     A filter that will be used to select files from within the specified folder.
	 * @param codec      The codec that will be used to decode the files to the specified type.
	 */
	public CodecDataLoader(Identifier id, String folderName, Predicate<Identifier> filter, Codec<? extends T> codec)
	{
		this.id = id;
		this.folderName = folderName;
		this.filter = filter;
		this.codec = codec;
		this.logger = Galaxies.createSubLogger("dataloader/" + id);
	}

	/**
	 * Gets the current set of data definitions associated with the loaded
	 * world, keyed by the identifier deriving from their filename
	 */
	public HashMap<Identifier, T> getDefinitions()
	{
		return definitions;
	}

	public Identifier getId()
	{
		return id;
	}

	@Override
	public void reload(ResourceManager manager)
	{
		definitions.clear();

		logger.info("Loading data...");

		var namespaces = new HashSet<String>();

		for (var entry : manager.findResources(folderName, filter).entrySet())
		{
			var key = entry.getKey();
			var resource = entry.getValue();

			logger.debug("Loading {}", key);

			try (
					var stream = resource.getInputStream();
					var reader = new InputStreamReader(stream)
			)
			{
				var parseResult = codec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader));
				if (parseResult.error().isPresent())
					throw new IOException("Failed to decode %s definition '%s' from JSON: %s".formatted(id, key, parseResult.error().get().message()));

				definitions.put(
						key.withPath(FilenameUtils.getBaseName(key.getPath())),
						parseResult.result().orElseThrow(() -> new IOException("Failed to decode %s definition '%s' from JSON".formatted(id, key)))
				);

				namespaces.add(key.getNamespace());
			}
			catch (Exception e)
			{
				logger.error("Failed to load " + key, e);
			}
		}

		logger.info("Loaded {} entries from the following namespaces: {}", definitions.size(), String.join(", ", namespaces));
	}
}

