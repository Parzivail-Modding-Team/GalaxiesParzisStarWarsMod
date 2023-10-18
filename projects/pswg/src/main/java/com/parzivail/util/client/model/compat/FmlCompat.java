package com.parzivail.util.client.model.compat;

import com.parzivail.util.runtime.ClassLoadingHelper;

public class FmlCompat
{
	public static boolean isForge()
	{
		return ClassLoadingHelper.exists("net.minecraftforge.fml.common.Mod");
	}
}
