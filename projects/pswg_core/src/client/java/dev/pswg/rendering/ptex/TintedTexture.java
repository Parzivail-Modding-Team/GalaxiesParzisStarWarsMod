package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;
import dev.pswg.Galaxies;
import dev.pswg.utility.ColorUtil;
import net.minecraft.resources.Identifier;

/**
 * A texture graph node that multiplies an upstream image by one tint color.
 *
 * @param tintColor The ARGB tint color.
 * @param upstream  The upstream texture graph.
 */
public record TintedTexture(int tintColor, PtexTextureSpec upstream) implements PtexTextureSpec
{
	/**
	 * The logger used by tinted textures.
	 */
	private static final org.slf4j.Logger LOGGER = Galaxies.createSubLogger("ptex/tint");

	/**
	 * Creates a tinted texture node.
	 *
	 * @param tintColor The ARGB tint color.
	 * @param upstream  The upstream texture graph.
	 */
	public TintedTexture
	{
		if (upstream == null)
		{
			throw new IllegalArgumentException("Tinted texture upstream must not be null");
		}
	}

	/**
	 * Creates a tinted texture node.
	 *
	 * @param tintColor The ARGB tint color.
	 * @param upstream  The upstream texture graph.
	 *
	 * @return The tinted texture node.
	 */
	public static TintedTexture tint(int tintColor, PtexTextureSpec upstream)
	{
		return new TintedTexture(tintColor, upstream);
	}

	/**
	 * Creates a tinted texture node from one direct texture identifier.
	 *
	 * @param tintColor  The ARGB tint color.
	 * @param identifier The direct source texture identifier.
	 *
	 * @return The tinted texture node.
	 */
	public static TintedTexture tint(int tintColor, Identifier identifier)
	{
		return tint(tintColor, SourceTexture.of(identifier));
	}

	@Override
	public String cacheKey()
	{
		return "tint(" + Integer.toUnsignedString(tintColor) + "," + upstream.cacheKey() + ")";
	}

	@Override
	public AsyncTexture createTexture(PtexTextureResolver resolver)
	{
		LOGGER.debug(
				"Creating tinted texture {} over {}",
				Integer.toUnsignedString(tintColor),
				upstream.cacheKey()
		);
		return resolver.transform(
				upstream,
				sourceImage -> tintImage(sourceImage, tintColor),
				cacheKey()
		);
	}

	/**
	 * Creates one tinted copy of an image.
	 *
	 * @param sourceImage The source image.
	 * @param tintColor   The ARGB tint color.
	 *
	 * @return The tinted image.
	 */
	private static NativeImage tintImage(NativeImage sourceImage, int tintColor)
	{
		LOGGER.debug(
				"Tinting image {}x{} with color {}",
				sourceImage.getWidth(),
				sourceImage.getHeight(),
				Integer.toUnsignedString(tintColor)
		);
		var tintedImage = new NativeImage(sourceImage.getWidth(), sourceImage.getHeight(), true);

		for (int y = 0; y < sourceImage.getHeight(); y++)
		{
			for (int x = 0; x < sourceImage.getWidth(); x++)
			{
				int pixel = sourceImage.getPixel(x, y);
				tintedImage.setPixel(x, y, ColorUtil.multiplyTint(pixel, tintColor));
			}
		}

		return tintedImage;
	}
}
