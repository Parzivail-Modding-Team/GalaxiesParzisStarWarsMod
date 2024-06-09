package com.parzivail.util;

import net.minecraft.util.Identifier;

public class ParziUtil
{
	public static final Identifier MODID = Identifier.of("pswg", "parzi_util");
	public static final Lumberjack LOG = new Lumberjack(MODID.toString());

	public static Identifier id(String path)
	{
		return MODID.withSuffixedPath("/" + path);
	}
}
