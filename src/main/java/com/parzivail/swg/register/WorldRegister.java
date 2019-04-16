package com.parzivail.swg.register;

import com.parzivail.swg.dimension.PlanetDescriptor;
import com.parzivail.swg.dimension.tatooine.BiomeTatooine;
import com.parzivail.swg.dimension.tatooine.WorldProviderTatooine;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;

public class WorldRegister
{
	public static int tatooineId = 2;
	public static Biome biomeTatooineDunes;
	public static DimensionType tatooineDimension;
	public static HashMap<Integer, PlanetDescriptor> planetDescriptorHashMap = new HashMap<>();

	public static void register()
	{
		tatooineDimension = DimensionType.register("Tatooine", "_tatooine", tatooineId, WorldProviderTatooine.class, false);
		biomeTatooineDunes = new BiomeTatooine();
		DimensionManager.registerDimension(tatooineId, tatooineDimension);
		BiomeManager.addBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(biomeTatooineDunes, 0));
		BiomeManager.removeSpawnBiome(biomeTatooineDunes);
		planetDescriptorHashMap.put(tatooineId, new PlanetDescriptor("Tatooine", 1, 1, 1));
	}
}
