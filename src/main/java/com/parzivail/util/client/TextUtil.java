package com.parzivail.util.client;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextUtil
{
	public static Text stylizeKeybind(Text text)
	{
		return Text.of(Formatting.GOLD + "[" + text.asString() + "]" + Formatting.RESET);
	}
}
