package com.parzivail.pswgtk.util;

import net.minecraft.util.Language;

public class LangUtil
{
	public static String translate(String key)
	{
		return Language.getInstance().get(key);
	}
}
