package dev.pswg.util.worldgen.biome;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Map;
import java.util.stream.Stream;

public class BiomeList
{
	private static final BiMap<Integer, TerrainBiome> BIOMES = HashBiMap.create();
	private static int index = 0;

	public static void register(TerrainBiome biome)
	{
		BIOMES.put(index++, biome);
	}

	public static TerrainBiome get(int id)
	{
		return BIOMES.get(id);
	}

	/**
	 * Gets the registered terrain biomes.
	 *
	 * @return the registered terrain biomes
	 */
	public static Stream<TerrainBiome> stream()
	{
		return BIOMES.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue);
	}

	public static int getId(TerrainBiome biome)
	{
		return BIOMES.inverse().get(biome);
	}
}
