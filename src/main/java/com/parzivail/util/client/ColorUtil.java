package com.parzivail.util.client;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.texture.NativeImage;

public class ColorUtil
{
	public static int blendColorsOnSrcAlpha(int dest, int src, int tint)
	{
		float destA = NativeImage.getAlpha(dest) / 255f;
		int destR = NativeImage.getRed(dest);
		int destG = NativeImage.getGreen(dest);
		int destB = NativeImage.getBlue(dest);

		float srcA = NativeImage.getAlpha(src) / 255f;
		int srcR = NativeImage.getRed(src);
		int srcG = NativeImage.getGreen(src);
		int srcB = NativeImage.getBlue(src);

		//		float tintA = NativeImage.getAlpha(tint) / 255f;
		int tintR = NativeImage.getRed(tint);
		int tintG = NativeImage.getGreen(tint);
		int tintB = NativeImage.getBlue(tint);

		srcR = (srcR * tintR) / 255;
		srcG = (srcG * tintG) / 255;
		srcB = (srcB * tintB) / 255;

		int a = com.parzivail.util.math.MathUtil.clamp((int)((destA + srcA) * 255f), 0, 255);
		int r = com.parzivail.util.math.MathUtil.clamp((int)((1 - srcA) * destR + srcA * srcR), 0, 255);
		int g = com.parzivail.util.math.MathUtil.clamp((int)((1 - srcA) * destG + srcA * srcG), 0, 255);
		int b = MathUtil.clamp((int)((1 - srcA) * destB + srcA * srcB), 0, 255);

		return NativeImage.getAbgrColor(a, b, g, r);
	}

	public static int packRgb(int r, int g, int b)
	{
		int rgb = (r & 0xFF);
		rgb = (rgb << 8) + (g & 0xFF);
		rgb = (rgb << 8) + (b & 0xFF);
		return rgb | 0xFF000000;
	}

	public static int packFloatRgb(final float r, final float g, final float b)
	{
		return packRgb((int)(r * 255 + 0.5),
		               (int)(g * 255 + 0.5),
		               (int)(b * 255 + 0.5));
	}

	public static int fromHSV(final float hue, final float saturation, final float value)
	{
		final float normaliedHue = (hue - (float)Math.floor(hue));
		final int h = (int)(normaliedHue * 6);
		final float f = normaliedHue * 6 - h;
		final float p = value * (1 - saturation);
		final float q = value * (1 - f * saturation);
		final float t = value * (1 - (1 - f) * saturation);

		return switch (h)
				{
					case 0 -> packFloatRgb(value, t, p);
					case 1 -> packFloatRgb(q, value, p);
					case 2 -> packFloatRgb(p, value, t);
					case 3 -> packFloatRgb(p, q, value);
					case 4 -> packFloatRgb(t, p, value);
					case 5 -> packFloatRgb(value, p, q);
					default -> 0;
				};
	}
}
