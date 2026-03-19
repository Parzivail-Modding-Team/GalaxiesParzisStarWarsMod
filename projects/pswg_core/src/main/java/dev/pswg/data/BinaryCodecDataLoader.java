package dev.pswg.data;

import dev.pswg.Galaxies;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * A datapack loader for packet-codec-backed data
 */
public class BinaryCodecDataLoader<T> implements PreparableReloadListener
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
	 * The reload listeners that must run before this loader.
	 */
	private final Collection<Identifier> dependencies;

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
	private final StreamCodec<ByteBuf, ? extends T> codec;

	/**
	 * Creates a new packet-codec-backed data loader
	 *
	 * @param id              The identifier for this data loader instance.
	 * @param dependencies    The reload listeners that must run before this loader.
	 * @param folderName      The path of the folder from which data will be loaded.
	 * @param removeExtension Whether the extension should be removed from entry keys.
	 * @param filter          A filter that will be used to select files from within the specified folder.
	 * @param codec           The codec that will be used to decode the files to the specified type.
	 */
	public BinaryCodecDataLoader(
			Identifier id,
			Collection<Identifier> dependencies,
			String folderName,
			boolean removeExtension,
			Predicate<Identifier> filter,
			StreamCodec<ByteBuf, ? extends T> codec
	)
	{
		this.id = id;
		this.dependencies = List.copyOf(dependencies);
		this.folderName = folderName;
		this.removeExtension = removeExtension;
		this.filter = filter;
		this.codec = codec;
		this.logger = Galaxies.createSubLogger("dataloader/" + id);
	}

	/**
	 * Creates a new packet-codec-backed data loader with no ordering requirements.
	 *
	 * @param id              The identifier for this data loader instance.
	 * @param folderName      The path of the folder from which data will be loaded.
	 * @param removeExtension Whether the extension should be removed from entry keys.
	 * @param filter          A filter that will be used to select files from within the specified folder.
	 * @param codec           The codec that will be used to decode the files to the specified type.
	 */
	public BinaryCodecDataLoader(
			Identifier id,
			String folderName,
			boolean removeExtension,
			Predicate<Identifier> filter,
			StreamCodec<ByteBuf, ? extends T> codec
	)
	{
		this(id, List.of(), folderName, removeExtension, filter, codec);
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

	/**
	 * Gets the reload listeners that must run before this loader.
	 *
	 * @return The reload listener ids that should precede this loader.
	 */
	public Collection<Identifier> getDependencies()
	{
		return dependencies;
	}

	@Override
	public CompletableFuture<Void> reload(SharedState store, Executor prepareExecutor, PreparationBarrier reloadSynchronizer, Executor applyExecutor)
	{
		return CompletableFuture
				.supplyAsync(() -> {
					this.reload(store.resourceManager());
					return Unit.INSTANCE;
				}, prepareExecutor)
				.thenCompose(reloadSynchronizer::wait)
				.thenAcceptAsync((reloadState) -> this.apply(), applyExecutor);
	}

	private void reload(ResourceManager manager)
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
					var stream = resource.open()
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

