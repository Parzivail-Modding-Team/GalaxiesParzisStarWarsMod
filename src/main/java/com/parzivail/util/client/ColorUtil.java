package com.parzivail.util.client;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.texture.NativeImage;

public class ColorUtil
{
	public static int blendColorsOnSrcAlpha(int dest, int src, int tint)
	{
		var destA = NativeImage.getAlpha(dest) / 255f;
		var destR = NativeImage.getRed(dest);
		var destG = NativeImage.getGreen(dest);
		var destB = NativeImage.getBlue(dest);

		var srcA = NativeImage.getAlpha(src) / 255f;
		var srcR = NativeImage.getRed(src);
		var srcG = NativeImage.getGreen(src);
		var srcB = NativeImage.getBlue(src);

		//		float tintA = NativeImage.getAlpha(tint) / 255f;
		var tintR = NativeImage.getRed(tint);
		var tintG = NativeImage.getGreen(tint);
		var tintB = NativeImage.getBlue(tint);

		srcR = (srcR * tintR) / 255;
		srcG = (srcG * tintG) / 255;
		srcB = (srcB * tintB) / 255;

		var a = com.parzivail.util.math.MathUtil.clamp((int)((destA + srcA) * 255f), 0, 255);
		var r = com.parzivail.util.math.MathUtil.clamp((int)((1 - srcA) * destR + srcA * srcR), 0, 255);
		var g = com.parzivail.util.math.MathUtil.clamp((int)((1 - srcA) * destG + srcA * srcG), 0, 255);
		var b = MathUtil.clamp((int)((1 - srcA) * destB + srcA * srcB), 0, 255);

		return NativeImage.getAbgrColor(a, b, g, r);
	}

	public static int packRgb(int r, int g, int b)
	{
		var rgb = (r & 0xFF);
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
		final var normaliedHue = (hue - (float)Math.floor(hue));
		final var h = (int)(normaliedHue * 6);
		final var f = normaliedHue * 6 - h;
		final var p = value * (1 - saturation);
		final var q = value * (1 - f * saturation);
		final var t = value * (1 - (1 - f) * saturation);

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
