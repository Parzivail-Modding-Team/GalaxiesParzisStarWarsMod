package dev.pswg.rendering.ptex;

import net.minecraft.resources.Identifier;

/**
 * Creates async images for immutable PSWG texture graphs.
 */
public interface PtexTextureResolver
{
	/**
	 * Loads one texture graph.
	 *
	 * @param textureSpec The immutable texture graph.
	 *
	 * @return The async image.
	 */
	PtexAsyncTexture load(PtexTextureSpec textureSpec);

	/**
	 * Loads one direct source texture.
	 *
	 * @param identifier The source texture identifier.
	 *
	 * @return The async image.
	 */
	PtexAsyncTexture loadSource(Identifier identifier);

	/**
	 * Transforms one upstream texture graph.
	 *
	 * @param upstream     The upstream texture graph.
	 * @param transform    The image transform.
	 * @param description  The texture description used in error logs.
	 *
	 * @return The async transformed image.
	 */
	PtexAsyncTexture transform(PtexTextureSpec upstream, PtexTextureTransform transform, String description);
}
