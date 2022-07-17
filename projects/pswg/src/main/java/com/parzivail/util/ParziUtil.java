package com.parzivail.util;

import net.minecraft.util.Identifier;

public class ParziUtil
{
	public static final String MODID = "parzi_util";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	public static Identifier id(String path)
	{
		return new Identifier(MODID, path);
	}
}
