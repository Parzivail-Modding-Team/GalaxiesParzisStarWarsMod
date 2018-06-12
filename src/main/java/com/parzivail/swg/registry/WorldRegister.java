package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.dimension.PlanetDescriptor;
import com.parzivail.swg.dimension.endor.BiomeEndor;
import com.parzivail.swg.dimension.endor.WorldProviderEndor;
import com.parzivail.swg.dimension.tatooine.BiomeTatooineDunes;
import com.parzivail.swg.dimension.tatooine.WorldProviderTatooine;
import com.parzivail.util.world.WorldUtils;
import net.minecraftforge.common.BiomeManager;

import java.util.HashMap;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldRegister
{
	public static BiomeTatooineDunes biomeTatooineDunes;
	public static BiomeEndor biomeEndor;

	public static HashMap<Integer, PlanetDescriptor> planetDescriptorHashMap = new HashMap<>();

	public static void register()
	{
		biomeTatooineDunes = new BiomeTatooineDunes(Resources.biomeIdTatooineDunes);
		BiomeManager.removeSpawnBiome(biomeTatooineDunes);

		biomeEndor = new BiomeEndor(Resources.biomeIdEndor);
		BiomeManager.removeSpawnBiome(biomeEndor);

		WorldUtils.registerDimension(Resources.dimIdTatooine, WorldProviderTatooine.class);
		WorldUtils.registerDimension(Resources.dimIdEndor, WorldProviderEndor.class);

		planetDescriptorHashMap.put(Resources.dimIdTatooine, new PlanetDescriptor("Tatooine"));
		planetDescriptorHashMap.put(Resources.dimIdEndor, new PlanetDescriptor("The Forest Moon of Endor"));
	}
}
