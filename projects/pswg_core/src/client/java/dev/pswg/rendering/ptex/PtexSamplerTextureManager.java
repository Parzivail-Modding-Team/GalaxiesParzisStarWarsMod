package dev.pswg.rendering.ptex;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import dev.pswg.Galaxies;
import dev.pswg.data.RegisterableResourceReloader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages runtime sampler-domain PSWG texture graphs.
 */
public final class PtexSamplerTextureManager implements ResourceManagerReloadListener, RegisterableResourceReloader
{
	/**
	 * The logger used by the runtime sampler texture system.
	 */
	private static final Logger LOGGER = Galaxies.createSubLogger("ptex/sampler");

	/**
	 * The executor used for background image generation.
	 */
	private static final Executor BACKGROUND_EXECUTOR = Util.nonCriticalIoPool().forName("pswg-ptex-sampler");

	/**
	 * The identifier used to register the client reload listener.
	 */
	private static final Identifier RELOADER_ID = Galaxies.id("ptex_sampler_textures");

	/**
	 * The active async sampler textures keyed by their immutable texture spec.
	 */
	private final ConcurrentHashMap<PtexTextureSpec, AsyncTexture> _asyncTextures = new ConcurrentHashMap<>();

	/**
	 * The active runtime texture entries keyed by their immutable texture spec.
	 */
	private final ConcurrentHashMap<PtexTextureSpec, RuntimeTexture> _textures = new ConcurrentHashMap<>();

	/**
	 * A monotonically increasing counter used to invalidate in-flight work on
	 * resource reload.
	 */
	private final AtomicInteger _reloadGeneration = new AtomicInteger();

	/**
	 * Prevents outside instantiation.
	 */
	PtexSamplerTextureManager()
	{
	}

	/**
	 * Gets the reload-listener identifier for this manager.
	 *
	 * @return The reloader identifier.
	 */
	@Override
	public Identifier getId()
	{
		return RELOADER_ID;
	}

	/**
	 * Gets the reload listeners that must run before this manager.
	 *
	 * @return The reload dependencies.
	 */
	@Override
	public Collection<Identifier> getDependencies()
	{
		return List.of();
	}

	/**
	 * Resolves one texture spec to a stable runtime sampled texture id.
	 *
	 * @param textureSpec The immutable texture graph.
	 *
	 * @return The runtime texture id if the graph can be prepared.
	 */
	public Optional<Identifier> resolve(PtexTextureSpec textureSpec)
	{
		if (textureSpec == null)
		{
			return Optional.empty();
		}

		if (textureSpec instanceof SourceTexture sourceTexture)
		{
			return Optional.of(sourceTexture.identifier());
		}

		try
		{
			var generation = _reloadGeneration.get();
			var asyncTexture = _asyncTextures.computeIfAbsent(textureSpec, ignored -> {
				LOGGER.debug("Creating async sampler texture {} in generation {}", textureSpec.cacheKey(), generation);
				return createAsyncTexture(textureSpec, generation);
			});
			var runtimeTexture = _textures.computeIfAbsent(textureSpec, ignored -> {
				LOGGER.debug("Creating runtime sampler texture {} in generation {}", textureSpec.cacheKey(), generation);
				return createResolvedTexture(textureSpec, asyncTexture);
			});
			if (!runtimeTexture._ready)
			{
				return Optional.empty();
			}

			return Optional.of(runtimeTexture._runtimeIdentifier);
		}
		catch (RuntimeException exception)
		{
			LOGGER.error("Failed to resolve sampler texture {}", textureSpec.cacheKey(), exception);
			return Optional.empty();
		}
	}

	/**
	 * Loads one texture spec as an async image chain.
	 *
	 * @param textureSpec The immutable texture graph.
	 *
	 * @return The async texture if the graph can be prepared.
	 */
	public Optional<AsyncTexture> load(PtexTextureSpec textureSpec)
	{
		if (textureSpec == null)
		{
			return Optional.empty();
		}

		try
		{
			var generation = _reloadGeneration.get();
			return Optional.of(_asyncTextures.computeIfAbsent(textureSpec, ignored -> {
				LOGGER.debug("Creating cached async sampler load {} in generation {}", textureSpec.cacheKey(), generation);
				return createAsyncTexture(textureSpec, generation);
			}));
		}
		catch (RuntimeException exception)
		{
			LOGGER.error("Failed to load sampler texture {}", textureSpec.cacheKey(), exception);
			return Optional.empty();
		}
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		LOGGER.debug(
				"Reloading ptex sampler manager; closing {} runtime textures and {} async textures",
				_textures.size(),
				_asyncTextures.size()
		);
		_reloadGeneration.incrementAndGet();

		var textureManager = Minecraft.getInstance().getTextureManager();
		for (var entry : _textures.values())
		{
			textureManager.release(entry._runtimeIdentifier);
		}

		_textures.clear();

		for (var texture : _asyncTextures.values())
		{
			texture.close();
		}

		_asyncTextures.clear();
	}

	/**
	 * Creates a resolved runtime sampler texture entry.
	 *
	 * @param textureSpec  The immutable texture graph.
	 * @param asyncTexture The composed async image backing the runtime texture.
	 *
	 * @return The new resolved runtime texture entry.
	 */
	private RuntimeTexture createResolvedTexture(PtexTextureSpec textureSpec, AsyncTexture asyncTexture)
	{
		var minecraft = Minecraft.getInstance();
		var runtimeIdentifier = createRuntimeIdentifier(textureSpec);
		var generation = _reloadGeneration.get();
		var runtimeTexture = new RuntimeTexture(runtimeIdentifier, generation);
		LOGGER.debug(
				"Registering placeholder runtime sampler texture {} for {} in generation {}",
				runtimeIdentifier,
				textureSpec.cacheKey(),
				generation
		);

		minecraft.getTextureManager().register(runtimeIdentifier, new DynamicTexture(runtimeIdentifier::toString, createPlaceholderImage()));

		asyncTexture
				.getFuture()
				.thenAccept(image -> {
					LOGGER.debug(
							"Sampler texture {} completed async generation at {}x{}",
							textureSpec.cacheKey(),
							image.getWidth(),
							image.getHeight()
					);
					minecraft.execute(() -> applyLoadedTexture(textureSpec, runtimeTexture, image));
				})
				.exceptionally(throwable -> {
					LOGGER.error("Failed to generate sampler texture {}", textureSpec.cacheKey(), throwable);
					return null;
				});

		return runtimeTexture;
	}

	/**
	 * Applies a generated image to the stable runtime texture entry.
	 *
	 * @param textureSpec       The immutable texture graph.
	 * @param runtimeTexture    The runtime texture entry receiving the image.
	 * @param image             The generated image to upload.
	 */
	private void applyLoadedTexture(PtexTextureSpec textureSpec, RuntimeTexture runtimeTexture, NativeImage image)
	{
		var runtimeIdentifier = runtimeTexture._runtimeIdentifier;
		var generation = runtimeTexture._generation;
		if (generation != _reloadGeneration.get())
		{
			LOGGER.debug("Skipping stale sampler upload for {} from generation {}", runtimeIdentifier, generation);
			return;
		}

		var current = _textures.get(textureSpec);
		if (current != null && current != runtimeTexture)
		{
			LOGGER.debug("Skipping sampler upload for {} because runtime texture entry was replaced", runtimeIdentifier);
			return;
		}

		var textureManager = Minecraft.getInstance().getTextureManager();
		var texture = textureManager.getTexture(runtimeIdentifier);

		if (texture instanceof DynamicTexture dynamicTexture)
		{
			var existingPixels = dynamicTexture.getPixels();
			if (existingPixels.getWidth() != image.getWidth() || existingPixels.getHeight() != image.getHeight())
			{
				LOGGER.debug(
						"Replacing runtime sampler texture {} from {}x{} to {}x{}",
						runtimeIdentifier,
						existingPixels.getWidth(),
						existingPixels.getHeight(),
						image.getWidth(),
						image.getHeight()
				);
				textureManager.register(runtimeIdentifier, new DynamicTexture(runtimeIdentifier::toString, copyImage(image)));
				runtimeTexture._ready = true;
				LOGGER.debug("Runtime sampler texture {} is now ready", runtimeIdentifier);
				return;
			}

			dynamicTexture.setPixels(copyImage(image));
			dynamicTexture.upload();
			runtimeTexture._ready = true;
			LOGGER.debug("Updated runtime sampler texture {} in place", runtimeIdentifier);
			return;
		}

		LOGGER.debug("Replacing missing/non-dynamic runtime texture registration for {}", runtimeIdentifier);
		textureManager.register(runtimeIdentifier, new DynamicTexture(runtimeIdentifier::toString, copyImage(image)));
		runtimeTexture._ready = true;
		LOGGER.debug("Runtime sampler texture {} is now ready", runtimeIdentifier);
	}

	/**
	 * Creates the stable runtime identifier for one immutable texture graph.
	 *
	 * @param textureSpec The immutable texture graph.
	 *
	 * @return The runtime texture identifier used by {@link DynamicTexture}.
	 */
	private static Identifier createRuntimeIdentifier(PtexTextureSpec textureSpec)
	{
		String hash = Hashing.sha1().hashUnencodedChars(textureSpec.cacheKey()).toString();
		return Galaxies.id("runtime/ptex/sampler/" + hash);
	}

	/**
	 * Creates the root async texture that loads one source image.
	 *
	 * @param identifier The source image identifier.
	 * @param generation The reload generation the request belongs to.
	 *
	 * @return The async source texture.
	 */
	private AsyncTexture createSourceTexture(Identifier identifier, int generation)
	{
		LOGGER.debug("Creating source sampler texture {} in generation {}", identifier, generation);
		return new AsyncTexture(
				CompletableFuture.supplyAsync(
						() -> loadSourceImage(identifier, generation),
						PtexSamplerTextureManager.BACKGROUND_EXECUTOR
				),
				null
		);
	}

	/**
	 * Creates one async texture for one immutable texture graph.
	 *
	 * @param textureSpec The immutable texture graph.
	 * @param generation  The reload generation the request belongs to.
	 *
	 * @return The async texture.
	 */
	private AsyncTexture createAsyncTexture(PtexTextureSpec textureSpec, int generation)
	{
		LOGGER.debug("Creating async texture implementation for {} in generation {}", textureSpec.cacheKey(), generation);
		return textureSpec.createTexture(createResolver(generation));
	}

	/**
	 * Creates a generation-bound resolver for one resource-reload cycle.
	 *
	 * @param generation The reload generation the request belongs to.
	 *
	 * @return The generation-bound resolver.
	 */
	private PtexTextureResolver createResolver(int generation)
	{
		return new GenerationResolver(generation);
	}

	/**
	 * Creates the placeholder image displayed until the real texture is ready.
	 *
	 * @return The placeholder image.
	 */
	private static NativeImage createPlaceholderImage()
	{
		var image = new NativeImage(2, 2, true);
		int light = ARGB.color(255, 255, 0, 255);
		int dark = ARGB.color(255, 0, 0, 0);

		image.setPixel(0, 0, light);
		image.setPixel(1, 0, dark);
		image.setPixel(0, 1, dark);
		image.setPixel(1, 1, light);

		return image;
	}

	/**
	 * Creates a copy of a native image so the runtime texture upload path does
	 * not take ownership of the cached async image.
	 *
	 * @param image The source image.
	 *
	 * @return The copied image.
	 */
	public static NativeImage copyImage(NativeImage image)
	{
		var copy = new NativeImage(image.getWidth(), image.getHeight(), true);

		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				copy.setPixel(x, y, image.getPixel(x, y));
			}
		}

		return copy;
	}

	/**
	 * The active runtime state for a resolved sampler texture.
	 */
	private static final class RuntimeTexture
	{
		/**
		 * The stable runtime texture identifier.
		 */
		private final Identifier _runtimeIdentifier;

		/**
		 * The reload generation the entry belongs to.
		 */
		private final int _generation;

		/**
		 * Whether the runtime texture contains a generated image instead of the
		 * initial unresolved placeholder.
		 */
		private volatile boolean _ready;

		/**
		 * Creates a new runtime texture entry.
		 *
		 * @param runtimeIdentifier The stable runtime texture identifier.
		 * @param generation        The reload generation the entry belongs to.
		 */
		private RuntimeTexture(Identifier runtimeIdentifier, int generation)
		{
			_runtimeIdentifier = runtimeIdentifier;
			_generation = generation;
		}
	}

	private final class GenerationResolver implements PtexTextureResolver
	{
		/**
		 * The reload generation the resolver belongs to.
		 */
		private final int _generation;

		/**
		 * Creates a generation-bound resolver.
		 *
		 * @param generation The reload generation.
		 */
		private GenerationResolver(int generation)
		{
			_generation = generation;
		}

		@Override
		public AsyncTexture load(PtexTextureSpec textureSpec)
		{
			return _asyncTextures.computeIfAbsent(textureSpec, ignored -> {
				LOGGER.debug("Resolver loading nested texture {} in generation {}", textureSpec.cacheKey(), _generation);
				return createAsyncTexture(textureSpec, _generation);
			});
		}

		@Override
		public AsyncTexture loadSource(Identifier identifier)
		{
			LOGGER.debug("Resolver loading source {} in generation {}", identifier, _generation);
			return createSourceTexture(identifier, _generation);
		}

		@Override
		public AsyncTexture transform(PtexTextureSpec upstreamSpec, PtexTextureTransform transform, String description)
		{
			LOGGER.debug("Resolver transforming {} via {}", upstreamSpec.cacheKey(), description);
			var upstream = load(upstreamSpec);

			CompletableFuture<NativeImage> future;

			var currentImage = upstream.getNow();
			if (currentImage.isPresent())
			{
				try
				{
					LOGGER.debug("Transform {} completed immediately", description);
					future = CompletableFuture.completedFuture(transformImage(currentImage.get(), transform, description));
				}
				catch (RuntimeException exception)
				{
					future = CompletableFuture.failedFuture(exception);
				}
			}
			else
				future = upstream.getFuture()
						.thenApplyAsync(sourceImage -> transformImage(sourceImage, transform, description), BACKGROUND_EXECUTOR);

			return new AsyncTexture(future, upstream::close);
		}
	}

	/**
	 * Loads one source image from the current resource manager.
	 *
	 * @param identifier The source image identifier.
	 * @param generation The reload generation the request belongs to.
	 *
	 * @return The loaded image.
	 */
	private NativeImage loadSourceImage(Identifier identifier, int generation)
	{
		if (generation != _reloadGeneration.get())
		{
			LOGGER.debug("Returning placeholder image for stale source texture {} from generation {}", identifier, generation);
			return createPlaceholderImage();
		}

		try
		{
			LOGGER.debug("Loading source sampler image {}", identifier);
			var resource = Minecraft
					.getInstance()
					.getResourceManager()
					.getResource(identifier)
					.orElseThrow(() -> new FileNotFoundException("Missing texture resource " + identifier));

			try (var stream = resource.open())
			{
				var image = NativeImage.read(stream);
				LOGGER.debug("Loaded source sampler image {} at {}x{}", identifier, image.getWidth(), image.getHeight());
				return image;
			}
		}
		catch (Exception exception)
		{
			throw new IllegalStateException("Failed to load source sampler texture " + identifier, exception);
		}
	}

	/**
	 * Produces one transformed image.
	 *
	 * @param sourceImage The upstream image.
	 *
	 * @param transform   The image transform.
	 * @param description The texture description used in error logs.
	 *
	 * @return The transformed image.
	 */
	private NativeImage transformImage(NativeImage sourceImage, PtexTextureTransform transform, String description)
	{
		try
		{
			return transform.apply(sourceImage);
		}
		catch (RuntimeException exception)
		{
			throw new IllegalStateException("Failed to transform sampler texture " + description, exception);
		}
	}
}
