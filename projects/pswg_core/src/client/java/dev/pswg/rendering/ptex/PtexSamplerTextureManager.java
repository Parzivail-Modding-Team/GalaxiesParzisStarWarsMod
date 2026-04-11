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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages runtime sampler-domain {@code ptex} textures.
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
	 * The registered runtime sampler-domain services.
	 */
	private final HashMap<String, PtexSamplerTextureService> _services = new HashMap<>();

	/**
	 * The active runtime texture entries keyed by their original {@code ptex}
	 * identifier.
	 */
	private final ConcurrentHashMap<Identifier, PendingTexture> _textures = new ConcurrentHashMap<>();

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

	@Override
	public Collection<Identifier> getDependencies()
	{
		return List.of();
	}

	/**
	 * Resolves a potential {@code ptex} sampler identifier to a stable runtime
	 * texture identifier.
	 *
	 * @param identifier The identifier to resolve.
	 *
	 * @return The runtime texture identifier if the request is supported, or
	 *         the original identifier otherwise.
	 */
	public Identifier resolve(Identifier identifier)
	{
		Optional<PtexTextureReference> parsedReference = PtexTextureReference.parse(identifier);

		if (parsedReference.isEmpty())
		{
			return identifier;
		}

		PtexTextureReference reference = parsedReference.get();
		if (reference.getDomain() != PtexTextureDomain.SAMPLER)
		{
			return identifier;
		}

		if (reference.getServices().isEmpty())
		{
			return createSourceIdentifier(reference);
		}

		if (reference.getServices().size() > 1)
		{
			LOGGER.warn("Sampler ptex service chains are not yet supported: {}", identifier);
			return createSourceIdentifier(reference);
		}

		PtexSamplerTextureService service = _services.get(reference.getServices().get(0));
		if (service == null)
		{
			LOGGER.warn("Unsupported sampler ptex service '{}': {}", reference.getServices().get(0), identifier);
			return createSourceIdentifier(reference);
		}

		var pending = _textures.computeIfAbsent(identifier, ignored -> createPendingTexture(reference, service));
		return pending._runtimeIdentifier;
	}

	/**
	 * Registers a sampler-domain texture service.
	 *
	 * @param service The service to register.
	 */
	public void registerService(PtexSamplerTextureService service)
	{
		if (_services.containsKey(service.getServiceName()))
		{
			throw new IllegalStateException("Duplicate ptex sampler service '" + service.getServiceName() + "'");
		}

		_services.put(service.getServiceName(), service);

	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		_reloadGeneration.incrementAndGet();

		var textureManager = Minecraft.getInstance().getTextureManager();
		for (var entry : _textures.values())
		{
			textureManager.release(entry._runtimeIdentifier);
		}

		_textures.clear();
	}

	/**
	 * Creates and schedules a new pending runtime texture.
	 *
	 * @param reference The parsed {@code ptex} texture reference.
	 * @param service   The sampler service that will generate the final image.
	 *
	 * @return The new pending texture entry.
	 */
	private PendingTexture createPendingTexture(PtexTextureReference reference, PtexSamplerTextureService service)
	{
		var minecraft = Minecraft.getInstance();
		var runtimeIdentifier = createRuntimeIdentifier(reference);
		var placeholderTexture = new DynamicTexture(runtimeIdentifier::toString, createPlaceholderImage());
		var generation = _reloadGeneration.get();

		minecraft.getTextureManager().register(runtimeIdentifier, placeholderTexture);

		CompletableFuture
				.supplyAsync(() -> loadTexture(reference, service, generation), BACKGROUND_EXECUTOR)
				.thenAccept(image -> minecraft.execute(() -> applyLoadedTexture(reference, runtimeIdentifier, generation, image)))
				.exceptionally(throwable -> {
					LOGGER.error("Failed to generate sampler ptex texture {}", reference.getOriginalIdentifier(), throwable);
					return null;
				});

		return new PendingTexture(runtimeIdentifier, generation);
	}

	/**
	 * Produces the final texture image on a background thread.
	 *
	 * @param reference   The parsed texture reference.
	 * @param service     The sampler service used to build the image.
	 * @param generation  The reload generation the work belongs to.
	 *
	 * @return The generated image.
	 */
	private NativeImage loadTexture(PtexTextureReference reference, PtexSamplerTextureService service, int generation)
	{
		if (generation != _reloadGeneration.get())
		{
			return createPlaceholderImage();
		}

		try
		{
			LOGGER.debug("Sampler service {} providing texture {}", service.getServiceName(), reference.getOriginalIdentifier());
			return service.load(reference, Minecraft.getInstance().getResourceManager());
		}
		catch (Exception exception)
		{
			LOGGER.error("Failed to load sampler ptex texture {}", reference.getOriginalIdentifier(), exception);
			return createPlaceholderImage();
		}
	}

	/**
	 * Applies a generated image to the stable runtime texture entry.
	 *
	 * @param reference         The original parsed texture reference.
	 * @param runtimeIdentifier The runtime texture identifier.
	 * @param generation        The generation the completed work belongs to.
	 * @param image             The generated image to upload.
	 */
	private void applyLoadedTexture(PtexTextureReference reference, Identifier runtimeIdentifier, int generation, NativeImage image)
	{
		try
		{
			if (generation != _reloadGeneration.get())
			{
				return;
			}

			var current = _textures.get(reference.getOriginalIdentifier());
			if (current == null || current._generation != generation)
			{
				return;
			}

			var textureManager = Minecraft.getInstance().getTextureManager();
			var texture = textureManager.getTexture(runtimeIdentifier);

			if (texture instanceof DynamicTexture dynamicTexture)
			{
				var existingPixels = dynamicTexture.getPixels();
				if (existingPixels.getWidth() != image.getWidth() || existingPixels.getHeight() != image.getHeight())
				{
					textureManager.register(runtimeIdentifier, new DynamicTexture(runtimeIdentifier::toString, image));
					image = null;
					return;
				}

				dynamicTexture.setPixels(image);
				dynamicTexture.upload();
				image = null;
				return;
			}

			textureManager.register(runtimeIdentifier, new DynamicTexture(runtimeIdentifier::toString, image));
			image = null;
		}
		finally
		{
			if (image != null && !image.isClosed())
			{
				image.close();
			}
		}
	}

	/**
	 * Creates the stable runtime identifier for a parsed {@code ptex} request.
	 *
	 * @param reference The texture reference being resolved.
	 *
	 * @return The runtime texture identifier used by {@link DynamicTexture}.
	 */
	private static Identifier createRuntimeIdentifier(PtexTextureReference reference)
	{
		String hash = Hashing.sha1().hashUnencodedChars(reference.getOriginalIdentifier().toString()).toString();
		return Galaxies.id("runtime/ptex/sampler/" + hash);
	}

	/**
	 * Creates the underlying source texture identifier for a parsed sampler
	 * reference that does not require a runtime service.
	 *
	 * @param reference The parsed sampler reference.
	 *
	 * @return The underlying source texture identifier.
	 */
	private static Identifier createSourceIdentifier(PtexTextureReference reference)
	{
		return Identifier.fromNamespaceAndPath(reference.getSourceNamespace(), reference.getRawPath());
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
	 * The active runtime state for a resolved sampler-domain texture.
	 *
	 * @param _runtimeIdentifier The stable runtime texture identifier.
	 * @param _generation        The reload generation the entry belongs to.
	 */
	private record PendingTexture(Identifier _runtimeIdentifier, int _generation)
	{
	}
}
