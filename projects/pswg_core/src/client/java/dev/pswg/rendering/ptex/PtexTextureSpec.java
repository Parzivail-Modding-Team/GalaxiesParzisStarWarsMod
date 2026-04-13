package dev.pswg.rendering.ptex;

import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * An immutable declarative texture graph for PSWG runtime texture composition.
 */
public interface PtexTextureSpec
{
	/**
	 * Gets the stable cache key for this texture graph.
	 *
	 * @return The stable cache key.
	 */
	String cacheKey();

	/**
	 * Creates the async image implementation for this texture graph.
	 *
	 * @param resolver The resolver used to build dependent graphs.
	 *
	 * @return The async image implementation.
	 */
	PtexAsyncTexture createTexture(PtexTextureResolver resolver);

	/**
	 * Resolves this texture graph to one stable runtime sampled texture id.
	 *
	 * @return The resolved runtime texture id if the texture can be prepared.
	 */
	default Optional<Identifier> get()
	{
		return PtexTextures.resolveSampler(this);
	}

	/**
	 * Resolves this texture graph to one stable runtime sampled texture id or
	 * falls back to one existing texture id.
	 *
	 * @param fallbackTexture The fallback texture identifier.
	 *
	 * @return The resolved runtime texture id or the fallback texture.
	 */
	default Identifier getOrElse(Identifier fallbackTexture)
	{
		return get().orElse(fallbackTexture);
	}

	/**
	 * Loads this texture graph as one async image chain.
	 *
	 * @return The async image chain if the texture can be prepared.
	 */
	default Optional<PtexAsyncTexture> load()
	{
		return PtexTextures.loadSampler(this);
	}
}
