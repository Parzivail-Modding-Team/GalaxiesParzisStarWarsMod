package com.parzivail.util.client;

import com.parzivail.util.data.TintedIdentifier;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

public class ColorUtil
{
	public static int blendColorsOnSrcAlpha(int dest, int src, int tint, TintedIdentifier.Mode tintMode)
	{
		var destA = NativeImage.getAlpha(dest) / 255f;
		var destR = NativeImage.getRed(dest);
		var destG = NativeImage.getGreen(dest);
		var destB = NativeImage.getBlue(dest);

		src = tint(src, tint, tintMode);

		var srcA = NativeImage.getAlpha(src) / 255f;
		var srcR = NativeImage.getRed(src);
		var srcG = NativeImage.getGreen(src);
		var srcB = NativeImage.getBlue(src);

		var a = com.parzivail.util.math.MathUtil.clamp((int)((destA + srcA) * 255f), 0, 255);
		var r = com.parzivail.util.math.MathUtil.clamp((int)((1 - srcA) * destR + srcA * srcR), 0, 255);
		var g = com.parzivail.util.math.MathUtil.clamp((int)((1 - srcA) * destG + srcA * srcG), 0, 255);
		var b = MathUtil.clamp((int)((1 - srcA) * destB + srcA * srcB), 0, 255);

		return NativeImage.packColor(a, b, g, r);
	}

	public static int tint(int src, int tint, TintedIdentifier.Mode tintMode)
	{
		var srcA = NativeImage.getAlpha(src);
		var srcR = NativeImage.getRed(src);
		var srcG = NativeImage.getGreen(src);
		var srcB = NativeImage.getBlue(src);

		//		float tintA = NativeImage.getAlpha(tint) / 255f;
		var tintR = NativeImage.getRed(tint);
		var tintG = NativeImage.getGreen(tint);
		var tintB = NativeImage.getBlue(tint);

		switch (tintMode)
		{
			case Multiply -> {
				srcR = (srcR * tintR) / 255;
				srcG = (srcG * tintG) / 255;
				srcB = (srcB * tintB) / 255;
			}
			case Add -> {
				srcR = MathHelper.clamp(srcR + tintR, 0, 255);
				srcG = MathHelper.clamp(srcG + tintG, 0, 255);
				srcB = MathHelper.clamp(srcB + tintB, 0, 255);
			}
			case Overlay -> {
				var srcRf = srcR / 255f;
				var srcGf = srcG / 255f;
				var srcBf = srcB / 255f;

				var tintRf = tintR / 255f;
				var tintGf = tintG / 255f;
				var tintBf = tintB / 255f;

				srcR = (int)(255 * MathHelper.clamp(srcRf < 0.5 ? 2 * srcRf * tintRf : (1 - 2 * (1 - srcRf) * (1 - tintRf)), 0, 1));
				srcG = (int)(255 * MathHelper.clamp(srcGf < 0.5 ? 2 * srcGf * tintGf : (1 - 2 * (1 - srcGf) * (1 - tintGf)), 0, 1));
				srcB = (int)(255 * MathHelper.clamp(srcBf < 0.5 ? 2 * srcBf * tintBf : (1 - 2 * (1 - srcBf) * (1 - tintBf)), 0, 1));
			}
		}

		return NativeImage.packColor(srcA, srcB, srcG, srcR);
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

	public static int rgbaToAbgr(int value)
	{
		return NativeImage.packColor((value >>> 24) & 0xFF, (value) & 0xFF, (value >>> 8) & 0xFF, (value >>> 16) & 0xFF);
	}

	public static int fromHSV(final float hue, final float saturation, final float value)
	{
		final var normalizedHue = (hue - (float)Math.floor(hue));
		final var h = (int)(normalizedHue * 6);
		final var f = normalizedHue * 6 - h;
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
