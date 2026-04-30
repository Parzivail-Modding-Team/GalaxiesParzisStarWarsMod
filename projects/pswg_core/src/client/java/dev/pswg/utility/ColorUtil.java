package dev.pswg.utility;

import net.minecraft.util.ARGB;

/**
 * Utility methods for working with colors.
 */
public class ColorUtil
{
	/**
	 * Multiplies a source pixel by a tint color while preserving alpha.
	 *
	 * @param pixel     The source pixel.
	 * @param tintColor The tint color to multiply into the pixel.
	 *
	 * @return The tinted pixel.
	 */
	public static int multiplyTint(int pixel, int tintColor)
	{
		int alpha = ARGB.alpha(pixel) * ARGB.alpha(tintColor) / 255;
		int red = ARGB.red(pixel) * ARGB.red(tintColor) / 255;
		int green = ARGB.green(pixel) * ARGB.green(tintColor) / 255;
		int blue = ARGB.blue(pixel) * ARGB.blue(tintColor) / 255;

		return ARGB.color(alpha, red, green, blue);
	}

	/**
	 * Alpha-composites one top pixel over one bottom pixel.
	 *
	 * @param bottomPixel The bottom pixel.
	 * @param topPixel    The top pixel.
	 *
	 * @return The composited pixel.
	 */
	public static int alphaComposite(int bottomPixel, int topPixel)
	{
		float topAlpha = ARGB.alpha(topPixel) / 255.0f;
		float bottomAlpha = ARGB.alpha(bottomPixel) / 255.0f;
		float outAlpha = topAlpha + bottomAlpha * (1.0f - topAlpha);

		if (outAlpha <= 0.0f)
		{
			return 0;
		}

		int red = Math.round((ARGB.red(topPixel) * topAlpha + ARGB.red(bottomPixel) * bottomAlpha * (1.0f - topAlpha)) / outAlpha);
		int green = Math.round((ARGB.green(topPixel) * topAlpha + ARGB.green(bottomPixel) * bottomAlpha * (1.0f - topAlpha)) / outAlpha);
		int blue = Math.round((ARGB.blue(topPixel) * topAlpha + ARGB.blue(bottomPixel) * bottomAlpha * (1.0f - topAlpha)) / outAlpha);
		int alpha = Math.round(outAlpha * 255.0f);

		return ARGB.color(alpha, red, green, blue);
	}
}
