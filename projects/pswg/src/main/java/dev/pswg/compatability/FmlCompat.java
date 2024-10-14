package dev.pswg.compatability;

import dev.pswg.runtime.ClassLoadingHelper;

/**
 * A set of utilities that enhance compatability with Forge
 */
public final class FmlCompat
{
	/**
	 * Determines if the mod is currently running underneath, or alongside, Forge
	 *
	 * @return True if Forge is loaded on the classpath
	 */
	public static boolean isForge()
	{
		return ClassLoadingHelper.exists("net.minecraftforge.fml.common.Mod");
	}
}
