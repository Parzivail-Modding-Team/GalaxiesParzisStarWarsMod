package com.parzivail.util.client;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextUtil
{
	public static Text stylizeKeybind(Text text)
	{
		return Text.literal("[").append(text).append("]").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
	}
}
