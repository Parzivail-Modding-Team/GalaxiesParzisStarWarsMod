package com.parzivail.util.math;

import net.minecraft.util.math.Vec3f;

public class ColorUtil
{
	public static int packRgb(int r, int g, int b)
	{
		var rgb = (r & 0xFF);
		rgb = (rgb << 8) + (g & 0xFF);
		rgb = (rgb << 8) + (b & 0xFF);
		return rgb | 0xFF000000;
	}

	public static int packArgb(int r, int g, int b, int a)
	{
		var rgb = (a & 0xFF);
		rgb = (rgb << 8) + (r & 0xFF);
		rgb = (rgb << 8) + (g & 0xFF);
		rgb = (rgb << 8) + (b & 0xFF);
		return rgb;
	}

	public static int packFloatRgb(final float r, final float g, final float b)
	{
		return packFloatArgb(r, g, b, 0);
	}

	public static int packFloatArgb(final float r, final float g, final float b, final float a)
	{
		return packArgb((int)(r * 255),
		                (int)(g * 255),
		                (int)(b * 255),
		                (int)(a * 255));
	}

	public static int hsvToRgbInt(final float hue, final float saturation, final float value)
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

	public static Vec3f hsvToRgb(final float hue, final float saturation, final float value)
	{
		final var normalizedHue = (hue - (float)Math.floor(hue));
		final var h = (int)(normalizedHue * 6);
		final var f = normalizedHue * 6 - h;
		final var p = value * (1 - saturation);
		final var q = value * (1 - f * saturation);
		final var t = value * (1 - (1 - f) * saturation);

		return switch (h)
				{
					case 0 -> new Vec3f(value, t, p);
					case 1 -> new Vec3f(q, value, p);
					case 2 -> new Vec3f(p, value, t);
					case 3 -> new Vec3f(p, q, value);
					case 4 -> new Vec3f(t, p, value);
					case 5 -> new Vec3f(value, p, q);
					default -> new Vec3f(0, 0, 0);
				};
	}

	public static String toResourceId(int color)
	{
		return String.format("%06x", color & 0xFFFFFF);
	}
}
