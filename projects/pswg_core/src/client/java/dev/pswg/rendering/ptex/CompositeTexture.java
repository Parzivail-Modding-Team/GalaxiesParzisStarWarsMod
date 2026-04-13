package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;
import dev.pswg.Galaxies;
import dev.pswg.utility.ColorUtil;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

/**
 * A texture graph node that alpha-composites multiple layers in order.
 *
 * @param layers The ordered texture layers from bottom to top.
 */
public record CompositeTexture(List<PtexTextureSpec> layers) implements PtexTextureSpec
{
	/**
	 * The logger used by composite textures.
	 */
	private static final org.slf4j.Logger LOGGER = Galaxies.createSubLogger("ptex/composite");

	/**
	 * Creates a composite texture node.
	 *
	 * @param layers The ordered texture layers.
	 */
	public CompositeTexture
	{
		if (layers == null || layers.isEmpty())
		{
			throw new IllegalArgumentException("Composite texture must have at least one layer");
		}

		for (var layer : layers)
		{
			if (layer == null)
			{
				throw new IllegalArgumentException("Composite texture layers must not contain null");
			}
		}

		layers = List.copyOf(layers);
	}

	/**
	 * Starts a composite texture builder with one direct texture layer.
	 *
	 * @param identifier The first direct texture layer.
	 *
	 * @return The composite builder.
	 */
	public static Builder layer(Identifier identifier)
	{
		return builder().layer(identifier);
	}

	/**
	 * Starts a composite texture builder with one texture-spec layer.
	 *
	 * @param layer The first texture-spec layer.
	 *
	 * @return The composite builder.
	 */
	public static Builder layer(PtexTextureSpec layer)
	{
		return builder().layer(layer);
	}

	/**
	 * Creates a composite texture builder.
	 *
	 * @return The composite builder.
	 */
	public static Builder builder()
	{
		return new Builder();
	}

	@Override
	public String cacheKey()
	{
		var joiner = new StringJoiner(",", "composite(", ")");

		for (var layer : layers)
		{
			joiner.add(layer.cacheKey());
		}

		return joiner.toString();
	}

	@Override
	public PtexAsyncTexture createTexture(PtexTextureResolver resolver)
	{
		LOGGER.debug("Creating composite texture {} with {} layers", cacheKey(), layers.size());
		var layerTextures = layers.stream()
				.map(resolver::load)
				.toList();

		var immediateImages = layerTextures.stream()
				.map(PtexAsyncTexture::getNow)
				.toList();

		CompletableFuture<NativeImage> nextFuture;
		NativeImage nextReadyImage;

		if (immediateImages.stream().allMatch(Optional::isPresent))
		{
			try
			{
				nextReadyImage = compositeReadyImages(immediateImages);
				LOGGER.debug("Composite texture {} was ready immediately", cacheKey());
				nextFuture = CompletableFuture.completedFuture(nextReadyImage);
			}
			catch (RuntimeException exception)
			{
				nextReadyImage = null;
				nextFuture = CompletableFuture.failedFuture(exception);
			}
		}
		else
		{
			nextReadyImage = null;
			var futures = layerTextures.stream()
					.map(PtexAsyncTexture::getFuture)
					.toArray(CompletableFuture[]::new);

			nextFuture = CompletableFuture.allOf(futures)
			                           .thenApply(ignored -> compositeLayerFutures(layerTextures, cacheKey()));
		}

		final CompletableFuture<NativeImage> future = nextFuture;
		final NativeImage readyImage = nextReadyImage;

		return new PtexAsyncTexture()
		{
			/**
			 * The composed image if it is already ready.
			 */
			private volatile NativeImage _readyImage = readyImage;

			/**
			 * Whether this async texture has been closed.
			 */
			private volatile boolean _closed;

			/**
			 * The composed image future.
			 */
			private final CompletableFuture<NativeImage> _future = future.whenComplete((image, throwable) -> {
				if (throwable == null)
				{
					if (_closed)
					{
						if (image != null && !image.isClosed())
						{
							image.close();
						}

						return;
					}

					_readyImage = image;
				}
			});

			@Override
			public Optional<NativeImage> getNow()
			{
				return Optional.ofNullable(_readyImage);
			}

			@Override
			public CompletableFuture<NativeImage> getFuture()
			{
				return _future;
			}

			@Override
			public void close()
			{
				_closed = true;

				if (_readyImage != null && !_readyImage.isClosed())
				{
					_readyImage.close();
					_readyImage = null;
				}

				for (var layer : layerTextures)
				{
					layer.close();
				}
			}
		};
	}

	/**
	 * Alpha-composites one top image onto one bottom image.
	 *
	 * @param bottomImage The bottom image.
	 * @param topImage    The top image.
	 *
	 * @return The composited image.
	 */
	private static NativeImage compositeImages(NativeImage bottomImage, NativeImage topImage)
	{
		if (bottomImage.getWidth() != topImage.getWidth() || bottomImage.getHeight() != topImage.getHeight())
		{
			throw new IllegalArgumentException("Composite texture layers must share one image size");
		}

		var compositedImage = new NativeImage(bottomImage.getWidth(), bottomImage.getHeight(), true);

		for (int y = 0; y < bottomImage.getHeight(); y++)
		{
			for (int x = 0; x < bottomImage.getWidth(); x++)
			{
				compositedImage.setPixel(x, y, ColorUtil.alphaComposite(bottomImage.getPixel(x, y), topImage.getPixel(x, y)));
			}
		}

		return compositedImage;
	}

	/**
	 * Composites already-ready layer images.
	 *
	 * @param readyImages The ready layer images.
	 *
	 * @return The composited image.
	 */
	private static NativeImage compositeReadyImages(List<Optional<NativeImage>> readyImages)
	{
		LOGGER.debug("Compositing {} ready image layers", readyImages.size());
		NativeImage compositedImage = null;

		for (var readyImage : readyImages)
		{
			if (compositedImage == null)
			{
				compositedImage = PtexSamplerTextureManager.copyImage(readyImage.orElseThrow());
				continue;
			}

			var nextComposite = compositeImages(compositedImage, readyImage.orElseThrow());
			compositedImage.close();
			compositedImage = nextComposite;
		}

		return compositedImage;
	}

	/**
	 * Composites layer futures after they have all completed.
	 *
	 * @param layerTextures The resolved layer textures.
	 * @param description   The texture description used in error logs.
	 *
	 * @return The composited image.
	 */
	private static NativeImage compositeLayerFutures(List<PtexAsyncTexture> layerTextures, String description)
	{
		LOGGER.debug("Compositing completed async image layers for {}", description);
		try
		{
			NativeImage compositedImage = null;

			for (var layer : layerTextures)
			{
				var layerImage = layer.getFuture().join();
				if (compositedImage == null)
				{
					compositedImage = PtexSamplerTextureManager.copyImage(layerImage);
					continue;
				}

				var nextComposite = compositeImages(compositedImage, layerImage);
				compositedImage.close();
				compositedImage = nextComposite;
			}

			return compositedImage;
		}
		catch (RuntimeException exception)
		{
			throw new IllegalStateException("Failed to composite sampler texture " + description, exception);
		}
	}

	/**
	 * Builds one composite texture spec fluently.
	 */
	public static final class Builder
	{
		/**
		 * The ordered composite layers being accumulated.
		 */
		private final ArrayList<PtexTextureSpec> _layers = new ArrayList<>();

		/**
		 * Adds one direct texture layer.
		 *
		 * @param identifier The layer texture identifier.
		 *
		 * @return This builder.
		 */
		public Builder layer(Identifier identifier)
		{
			return layer(SourceTexture.of(identifier));
		}

		/**
		 * Adds one texture-spec layer.
		 *
		 * @param layer The layer texture spec.
		 *
		 * @return This builder.
		 */
		public Builder layer(PtexTextureSpec layer)
		{
			if (layer == null)
			{
				throw new IllegalArgumentException("Composite layer must not be null");
			}

			_layers.add(layer);
			return this;
		}

		/**
		 * Builds the composite texture spec.
		 *
		 * @return The composite texture spec.
		 */
		public CompositeTexture build()
		{
			return new CompositeTexture(_layers);
		}

		/**
		 * Resolves the composed texture to one runtime sampled texture id.
		 *
		 * @return The resolved runtime texture id if the texture can be prepared.
		 */
		public Optional<Identifier> get()
		{
			return PtexTextures.resolveSampler(build());
		}

		/**
		 * Resolves the composed texture to one runtime sampled texture id or
		 * falls back to one existing texture id.
		 *
		 * @param fallbackTexture The fallback texture identifier.
		 *
		 * @return The resolved runtime texture id or the fallback texture.
		 */
		public Identifier getOrElse(Identifier fallbackTexture)
		{
			return get().orElse(fallbackTexture);
		}
	}
}
