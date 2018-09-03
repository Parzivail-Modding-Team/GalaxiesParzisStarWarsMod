package com.parzivail.util.ui;

import org.newdawn.slick.TrueTypeFont;

public class TtfUtil
{
	public static void drawString(TrueTypeFont font, float x, float y, String whatchars, org.newdawn.slick.Color color)
	{
		String[] lines = whatchars.split("\n");
		float lineY = 0;
		for (String line : lines)
		{
			font.drawString(x, y + lineY, line, color);
			lineY += font.getLineHeight();
		}
	}

	public static int getWidth(TrueTypeFont font, String whatchars)
	{
		String[] lines = whatchars.split("\n");
		int width = -1;
		for (String line : lines)
			width = Math.max(width, font.getWidth(line));
		return width;
	}

	public static int getHeight(TrueTypeFont font, String whatchars)
	{
		String[] lines = whatchars.split("\n");
		return lines.length * font.getLineHeight();
	}
}
