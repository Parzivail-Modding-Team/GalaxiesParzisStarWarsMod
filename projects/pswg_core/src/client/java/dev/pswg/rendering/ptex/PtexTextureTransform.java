package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;

/**
 * Produces one transformed image from one upstream image.
 */
@FunctionalInterface
public interface PtexTextureTransform
{
	/**
	 * Applies the transform to one upstream image.
	 *
	 * @param sourceImage The upstream image.
	 *
	 * @return The transformed image.
	 */
	NativeImage apply(NativeImage sourceImage);
}
