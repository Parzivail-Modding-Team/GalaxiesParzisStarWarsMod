package com.parzivail.swg;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

/**
 * Created by colby on 9/10/2017.
 */
public class Resources
{
	public static final String MODID = "pswg";

	public static final int[] PANEL_LIGHT_COLORS = {
			0xF8E443, 0x0050A1, 0x4ACBFF, 0xD1A250, 0x42A904, 0xFB4B33, 0x000000
	};

	private static final int MAJOR = 1;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	public static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	public static int biomeIdTatooineDunes = 100;
	public static int biomeIdEndor = 101;

	private static int guiIdx = 1;
	public static final int GUI_BLASTER_WORKBENCH = guiIdx++;
	public static final int GUI_DIALOGUE = guiIdx++;

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

	public static String guiDot(String name)
	{
		return dot(MODID, dot("gui", name));
	}

	public static String itemDot(String name, String variant)
	{
		return dot("item", MODID, name, variant);
	}

	public static String containerDot(String name)
	{
		return dot("container", name);
	}

	public static String tileDot(String name)
	{
		return dot("tile", name);
	}

	public static String dot(String... params)
	{
		return String.join(".", params);
	}

	public static String modColon(String name)
	{
		return String.format("%s:%s", MODID, name);
	}

	public static ResourceLocation location(String path)
	{
		return new ResourceLocation(MODID, path);
	}

	public static String getKeyName(KeyBinding keyBinding)
	{
		return GameSettings.getKeyDisplayString(keyBinding.getKeyCode());
	}
}
