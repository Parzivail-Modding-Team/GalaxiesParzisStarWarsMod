package com.parzivail.pswg;

import net.minecraft.util.Identifier;

import java.util.Random;

public class Resources
{
	public static final String MODID = "pswg";
	public static final String NAME = "Galaxies: Parzi's Star Wars Mod";
	public static final boolean IS_DEBUG = System.getenv("PSWG_DEBUG") != null && Boolean.parseBoolean(System.getenv("PSWG_DEBUG"));

	public static final Random RANDOM = new Random();

	public static Identifier identifier(String path)
	{
		return new Identifier(MODID, path);
	}
}
