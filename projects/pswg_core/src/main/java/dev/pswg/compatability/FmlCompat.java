package dev.pswg.compatability;

import dev.pswg.runtime.ClassLoadingHelper;

/**
 * A set of utilities that enhance compatability with Forge
 */
public final class FmlCompat
{
	/**
	 * Determines if the mod is currently running underneath, or alongside,
	 * a version of Forge
	 *
	 * @return True if Forge, NeoForge, or Sinytra is loaded on the classpath
	 */
	public static boolean isForge()
	{
		return ClassLoadingHelper.exists("net.minecraftforge.fml.common.Mod")
		       || ClassLoadingHelper.exists("net.neoforged.neoforge.common.NeoForgeMod")
		       || ClassLoadingHelper.exists("org.sinytra.connector.mod.ConnectorMod");
	}
}
