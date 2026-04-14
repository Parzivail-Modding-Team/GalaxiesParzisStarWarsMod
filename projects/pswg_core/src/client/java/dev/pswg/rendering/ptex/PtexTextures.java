package dev.pswg.rendering.ptex;

import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * The public entrypoint for PSWG runtime texture composition.
 */
public final class PtexTextures
{
	/**
	 * The singleton sampler texture manager.
	 */
	public static final PtexSamplerTextureManager LOADER = new PtexSamplerTextureManager();

	/**
	 * Resolves one texture spec to one stable runtime sampled texture id.
	 *
	 * @param textureSpec The immutable texture graph.
	 *
	 * @return The resolved runtime texture id if the texture can be prepared.
	 */
	public static Optional<Identifier> resolveSampler(PtexTextureSpec textureSpec)
	{
		return LOADER.resolve(textureSpec);
	}

	/**
	 * Loads one texture spec as an async image chain.
	 *
	 * @param textureSpec The immutable texture graph.
	 *
	 * @return The async texture if the graph can be prepared.
	 */
	public static Optional<AsyncTexture> loadSampler(PtexTextureSpec textureSpec)
	{
		return LOADER.load(textureSpec);
	}
}
