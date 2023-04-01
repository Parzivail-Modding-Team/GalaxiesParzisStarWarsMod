package com.parzivail.util.client;

import com.parzivail.util.data.TintedIdentifier;
import com.parzivail.util.math.MathUtil;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class NativeImageUtil
{
	public static int blendColorsOnSrcAlpha(int dest, int src, int tint, TintedIdentifier.Mode tintMode)
	{
		var destA = ColorHelper.Abgr.getAlpha(dest) / 255f;
		var destR = ColorHelper.Abgr.getRed(dest);
		var destG = ColorHelper.Abgr.getGreen(dest);
		var destB = ColorHelper.Abgr.getBlue(dest);

		src = tint(src, tint, tintMode);

		var srcA = ColorHelper.Abgr.getAlpha(src) / 255f;
		var srcR = ColorHelper.Abgr.getRed(src);
		var srcG = ColorHelper.Abgr.getGreen(src);
		var srcB = ColorHelper.Abgr.getBlue(src);

		var a = MathUtil.clamp((int)((destA + srcA) * 255f), 0, 255);
		var r = MathUtil.clamp((int)((1 - srcA) * destR + srcA * srcR), 0, 255);
		var g = MathUtil.clamp((int)((1 - srcA) * destG + srcA * srcG), 0, 255);
		var b = MathUtil.clamp((int)((1 - srcA) * destB + srcA * srcB), 0, 255);

		return ColorHelper.Abgr.getAbgr(a, b, g, r);
	}

	public static int tint(int src, int tint, TintedIdentifier.Mode tintMode)
	{
		var srcA = ColorHelper.Abgr.getAlpha(src);
		var srcR = ColorHelper.Abgr.getRed(src);
		var srcG = ColorHelper.Abgr.getGreen(src);
		var srcB = ColorHelper.Abgr.getBlue(src);

		var tintA = ColorHelper.Abgr.getAlpha(tint);
		if (tintA == 0)
			tintA = 255;

		var tintR = ColorHelper.Abgr.getRed(tint);
		var tintG = ColorHelper.Abgr.getGreen(tint);
		var tintB = ColorHelper.Abgr.getBlue(tint);

		switch (tintMode)
		{
			case Multiply ->
			{
				srcA = (srcA * tintA) / 255;
				srcR = (srcR * tintR) / 255;
				srcG = (srcG * tintG) / 255;
				srcB = (srcB * tintB) / 255;
			}
			case Add ->
			{
				srcR = MathHelper.clamp(srcR + tintR, 0, 255);
				srcG = MathHelper.clamp(srcG + tintG, 0, 255);
				srcB = MathHelper.clamp(srcB + tintB, 0, 255);
			}
			case Overlay ->
			{
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

		return ColorHelper.Abgr.getAbgr(srcA, srcB, srcG, srcR);
	}

	public static int argbToAbgr(int value)
	{
		return ColorHelper.Abgr.getAbgr((value >>> 24) & 0xFF, (value) & 0xFF, (value >>> 8) & 0xFF, (value >>> 16) & 0xFF);
	}
}
