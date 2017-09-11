package com.parzivail.util.world;

import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldUtils
{
	/**
	 * Registers a dimension with FML
	 *
	 * @param dimId The Dimension ID
	 */
	public static void registerDimension(int dimId)
	{
		DimensionManager.registerDimension(dimId, dimId);
		FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimId);
		Lumberjack.log("World registered for " + WorldProvider.getProviderForDimension(dimId).getDimensionName());
	}

	/**
	 * Registers a dimension with FML
	 *
	 * @param dimId  The Dimension ID
	 * @param class1 The world provider for the dimension
	 */
	public static void registerDimension(int dimId, Class<? extends WorldProvider> class1)
	{
		DimensionManager.registerProviderType(dimId, class1, true);
		DimensionManager.registerDimension(dimId, dimId);
		// WorldServer s =
		// FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimId);
		Lumberjack.log("Provider and World registered for " + dimId);
	}
}
