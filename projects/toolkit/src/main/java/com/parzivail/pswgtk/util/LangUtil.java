package com.parzivail.pswgtk.util;

import net.minecraft.util.Language;

public class LangUtil
{
	public static String translate(String key)
	{
		return Language.getInstance().get(key);
	}

	public static String tryTranslate(String key)
	{
		if (key.charAt(0) == '$')
			return translate(key.substring(1));
		return key;
	}
}
