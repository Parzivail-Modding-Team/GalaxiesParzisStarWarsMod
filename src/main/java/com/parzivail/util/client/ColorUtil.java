package com.parzivail.util.client;

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

		int a = (int)((destA + srcA) * 255f);
		int r = (int)((1 - srcA) * destR + srcA * srcR);
		int b = (int)((1 - srcA) * destG + srcA * srcG);
		int g = (int)((1 - srcA) * destB + srcA * srcB);

		// This is supposedly an ABGR color but the colors are very wrong unless you pack it as an AGBR color
		return NativeImage.getAbgrColor(a, g, b, r);
	}
}
