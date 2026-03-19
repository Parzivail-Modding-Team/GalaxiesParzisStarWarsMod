package dev.pswg.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.pswg.Galaxies;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * A datapack loader for codec-backed data
 */
public class CodecDataLoader<T> implements ResourceManagerReloadListener
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
	 * Whether the extension should be removed from entry keys
	 */
	private final boolean removeExtension;

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
	 * @param id              The identifier for this data loader instance.
	 * @param folderName      The path of the folder from which data will be loaded.
	 * @param removeExtension Whether the extension should be removed from entry keys.
	 * @param filter          A filter that will be used to select files from within the specified folder.
	 * @param codec           The codec that will be used to decode the files to the specified type.
	 */
	public CodecDataLoader(Identifier id, String folderName, boolean removeExtension, Predicate<Identifier> filter, Codec<? extends T> codec)
	{
		this.id = id;
		this.folderName = folderName;
		this.removeExtension = removeExtension;
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
	public void onResourceManagerReload(ResourceManager manager)
	{
		definitions.clear();

		logger.info("Loading data...");

		var namespaces = new HashSet<String>();

		for (var entry : manager.listResources(folderName, filter).entrySet())
		{
			var key = entry.getKey();
			var resource = entry.getValue();

			logger.debug("Loading {}", key);

			try (
					var stream = resource.open();
					var reader = new InputStreamReader(stream)
			)
			{
				var parseResult = codec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader));
				if (parseResult.error().isPresent())
					throw new IOException("Failed to decode %s definition '%s' from JSON: %s".formatted(id, key, parseResult.error().get().message()));

				var path = PathUtil.makeRelative(key.getPath(), folderName);

				if (removeExtension)
					path = FilenameUtils.removeExtension(path);

				definitions.put(
						key.withPath(path),
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

