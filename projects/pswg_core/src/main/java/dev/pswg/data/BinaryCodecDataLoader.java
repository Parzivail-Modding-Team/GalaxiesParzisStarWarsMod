package dev.pswg.data;

import dev.pswg.Galaxies;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * A datapack loader for packet-codec-backed data
 */
public class BinaryCodecDataLoader<T> implements ResourceReloader
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
	private final PacketCodec<ByteBuf, ? extends T> codec;

	/**
	 * Creates a new packet-codec-backed data loader
	 *
	 * @param id              The identifier for this data loader instance.
	 * @param folderName      The path of the folder from which data will be loaded.
	 * @param removeExtension Whether the extension should be removed from entry keys.
	 * @param filter          A filter that will be used to select files from within the specified folder.
	 * @param codec           The codec that will be used to decode the files to the specified type.
	 */
	public BinaryCodecDataLoader(Identifier id, String folderName, boolean removeExtension, Predicate<Identifier> filter, PacketCodec<ByteBuf, ? extends T> codec)
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
	public CompletableFuture<Void> reload(Store store, Executor prepareExecutor, Synchronizer reloadSynchronizer, Executor applyExecutor)
	{
		return CompletableFuture
				.supplyAsync(() -> {
					this.reload(store.getResourceManager());
					return Unit.INSTANCE;
				}, prepareExecutor)
				.thenCompose(reloadSynchronizer::whenPrepared)
				.thenAcceptAsync((reloadState) -> this.apply(), applyExecutor);
	}

	private void reload(ResourceManager manager)
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
					var stream = resource.getInputStream()
			)
			{
				var buf = Unpooled.wrappedBuffer(stream.readAllBytes());

				T parseResult;

				try
				{
					parseResult = codec.decode(buf);
				}
				catch (Exception e)
				{
					throw new IOException("Failed to decode %s definition '%s' from JSON: %s".formatted(id, key, e.getMessage()));
				}

				var path = PathUtil.makeRelative(key.getPath(), folderName);

				if (removeExtension)
					path = FilenameUtils.removeExtension(path);

				definitions.put(
						key.withPath(path),
						parseResult
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

	private void apply()
	{
		// Nothing to do
	}
}

