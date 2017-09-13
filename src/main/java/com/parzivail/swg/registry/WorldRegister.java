package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.dimension.tatooine.BiomeTatooineDunes;
import com.parzivail.swg.dimension.tatooine.WorldProviderTatooine;
import com.parzivail.util.world.WorldUtils;
import net.minecraftforge.common.BiomeManager;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldRegister
{
	public static BiomeTatooineDunes biomeTatooineDunes;

	public static void register()
	{
		biomeTatooineDunes = new BiomeTatooineDunes(Resources.biomeIdTatooineDunes);
		BiomeManager.removeSpawnBiome(biomeTatooineDunes);

		WorldUtils.registerDimension(Resources.dimIdTatooine, WorldProviderTatooine.class);
	}
}
