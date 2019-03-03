package com.parzivail.swg.registry;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.PlanetDescriptor;
import com.parzivail.swg.dimension.endor.BiomeEndor;
import com.parzivail.swg.dimension.endor.WorldProviderEndor;
import com.parzivail.swg.dimension.hyperspace.BiomeHyperspace;
import com.parzivail.swg.dimension.hyperspace.WorldProviderHyperspace;
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
	public static BiomeHyperspace biomeHyperspace;

	public static HashMap<Integer, PlanetDescriptor> planetDescriptorHashMap = new HashMap<>();

	public static void register()
	{
		biomeTatooineDunes = new BiomeTatooineDunes(StarWarsGalaxy.config.getBiomeIdTatooineDunes());
		BiomeManager.removeSpawnBiome(biomeTatooineDunes);

		biomeEndor = new BiomeEndor(StarWarsGalaxy.config.getBiomeIdEndor());
		BiomeManager.removeSpawnBiome(biomeEndor);

		biomeHyperspace = new BiomeHyperspace(StarWarsGalaxy.config.getBiomeIdHyperspace());
		BiomeManager.removeSpawnBiome(biomeHyperspace);

		WorldUtils.registerDimension(StarWarsGalaxy.config.getDimIdTatooine(), WorldProviderTatooine.class);
		WorldUtils.registerDimension(StarWarsGalaxy.config.getDimIdEndor(), WorldProviderEndor.class);
		WorldUtils.registerDimension(StarWarsGalaxy.config.getDimIdHyperspace(), WorldProviderHyperspace.class);

		planetDescriptorHashMap.put(StarWarsGalaxy.config.getDimIdTatooine(), new PlanetDescriptor("Tatooine", 23, 10465, 1));
		planetDescriptorHashMap.put(StarWarsGalaxy.config.getDimIdEndor(), new PlanetDescriptor("The Forest Moon of Endor", 18, 4900, 0.85f));
		planetDescriptorHashMap.put(StarWarsGalaxy.config.getDimIdHyperspace(), new PlanetDescriptor("Hyperspace", 0, 0, 0f));
	}
}
