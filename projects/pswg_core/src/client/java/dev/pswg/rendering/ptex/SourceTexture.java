package dev.pswg.rendering.ptex;

import dev.pswg.Galaxies;
import net.minecraft.resources.Identifier;

/**
 * A texture graph node that directly references one texture resource.
 *
 * @param identifier The source texture identifier.
 */
public record SourceTexture(Identifier identifier) implements PtexTextureSpec
{
	/**
	 * The logger used by direct source textures.
	 */
	private static final org.slf4j.Logger LOGGER = Galaxies.createSubLogger("ptex/source");

	/**
	 * Creates a direct source texture node.
	 *
	 * @param identifier The source texture identifier.
	 */
	public SourceTexture
	{
		if (identifier == null)
		{
			throw new IllegalArgumentException("Source texture identifier must not be null");
		}
	}

	/**
	 * Creates a direct source texture node.
	 *
	 * @param identifier The source texture identifier.
	 *
	 * @return The source texture node.
	 */
	public static SourceTexture of(Identifier identifier)
	{
		return new SourceTexture(identifier);
	}

	@Override
	public String cacheKey()
	{
		return "source(" + identifier + ")";
	}

	@Override
	public PtexAsyncTexture createTexture(PtexTextureResolver resolver)
	{
		LOGGER.debug("Creating source texture {}", identifier);
		return resolver.loadSource(identifier);
	}
}
