package com.parzivail.pswgtk.util;

public record Color3b(byte r, byte g, byte b)
{
	public Color3b(int r, int g, int b)
	{
		this((byte)r, (byte)g, (byte)b);
	}

	public String toHex()
	{
		return "%02X%02X%02X".formatted(r, g, b);
	}

	public int toArgb(byte a)
	{
		int x = a;
		x = (x << 8) | r;
		x = (x << 8) | g;
		return (x << 8) | b;
	}
}
