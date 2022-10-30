package com.parzivail.util.gen.biome;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class BiomeList
{
	private static final BiMap<Integer, TerrainBiome> BIOMES = HashBiMap.create();
	private static int index = 0;

	public static void register(TerrainBiome biome) {
		BIOMES.put(index++, biome);
	}

	public static TerrainBiome get(int id) {
		return BIOMES.get(id);
	}

	public static int getId(TerrainBiome biome) {
		return BIOMES.inverse().get(biome);
	}
}
