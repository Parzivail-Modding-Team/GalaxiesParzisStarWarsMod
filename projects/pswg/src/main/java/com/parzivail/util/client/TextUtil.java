package com.parzivail.util.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextUtil
{
	public static Text stylizeKeybind(Text text)
	{
		return Text.literal("[").append(text).append("]").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
	}

	public static void drawArea(MatrixStack matrices, TextRenderer renderer, int x, int y, int width, Text text)
	{
		var lines = renderer.wrapLines(text, width);

		for (var m = 0; m < lines.size(); ++m)
			renderer.draw(matrices, lines.get(m), x, y + m * renderer.fontHeight, 0);
	}
}
