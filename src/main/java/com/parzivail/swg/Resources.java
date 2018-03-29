package com.parzivail.swg;

/**
 * Created by colby on 9/10/2017.
 */
public class Resources
{
	private static final int MAJOR = 0;
	private static final int MINOR = 0;
	private static final int PATCH = 1;
	public static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;
	public static final String MODID = "pswg";

	public static int dimIdTatooine = 2;
	public static int biomeIdTatooineDunes = 100;

	public static int dimIdNaboo = 3;
	public static int biomeIdNaboo = 101;

	public static String itemDot(String name)
	{
		return dot("item", MODID, name);
	}

	public static String blockDot(String name)
	{
		return dot("block", MODID, name);
	}

	public static String modDot(String name)
	{
		return dot(MODID, name);
	}

	public static String modDot(String... name)
	{
		return dot(MODID, dot(name));
	}

	public static String itemDot(String name, String variant)
	{
		return dot("item", MODID, name, variant);
	}

	public static String dot(String... params)
	{
		return String.join(".", params);
	}

	public static String modColon(String name)
	{
		return String.format("%s:%s", MODID, name);
	}
}
