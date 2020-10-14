package com.parzivail.pswg;

import com.parzivail.util.noise.OpenSimplex2F;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.Random;

public class Resources
{
	public static final String MODID = "pswg";
	public static final String NAME = "Galaxies: Parzi's Star Wars Mod";
	public static final boolean IS_DEBUG = System.getenv("PSWG_DEBUG") != null && Boolean.parseBoolean(System.getenv("PSWG_DEBUG"));

	public static final OpenSimplex2F SIMPLEX_0 = new OpenSimplex2F(0);
	public static final Random RANDOM = new Random();

	public static Identifier identifier(@Nonnull String path)
	{
		return new Identifier(MODID, path);
	}

	public static String modcolon(String str)
	{
		return MODID + ":" + str;
	}
}
