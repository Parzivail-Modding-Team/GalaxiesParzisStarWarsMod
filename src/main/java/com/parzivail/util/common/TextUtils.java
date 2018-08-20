package com.parzivail.util.common;

public class TextUtils
{
	public static String scrambleString(String s, double percent)
	{
		if (percent > 1)
			percent = 1;
		if (percent < 0)
			percent = 0;

		return s.substring(0, (int)(s.length() * percent)) + (percent < 1 ? "_" : "");
	}
}
