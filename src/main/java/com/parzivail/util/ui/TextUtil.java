package com.parzivail.util.ui;

import net.minecraft.client.gui.FontRenderer;

public class TextUtil
{
	public static String scrambleString(String s, double percent)
	{
		if (percent > 1)
			percent = 1;
		if (percent < 0)
			percent = 0;

		return s.substring(0, (int)(s.length() * percent)) + (percent < 1 ? "_" : "");
	}

	public static void drawString(FontRenderer font, String whatchars, int x, int y, int color)
	{
		String[] lines = whatchars.split("\n");
		int lineY = 0;
		for (String line : lines)
		{
			font.drawString(line, x, y + lineY, color);
			lineY += font.FONT_HEIGHT;
		}
	}

	public static int getWidth(FontRenderer font, String whatchars)
	{
		String[] lines = whatchars.split("\n");
		int width = -1;
		for (String line : lines)
			width = Math.max(width, font.getStringWidth(line));
		return width;
	}

	public static int getHeight(FontRenderer font, String whatchars)
	{
		String[] lines = whatchars.split("\n");
		return lines.length * font.FONT_HEIGHT;
	}
}
