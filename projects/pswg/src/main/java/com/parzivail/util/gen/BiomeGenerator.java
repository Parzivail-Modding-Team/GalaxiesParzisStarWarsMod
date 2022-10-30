package com.parzivail.util.gen;

import com.parzivail.util.gen.biome.TerrainBiome;
import com.parzivail.util.gen.biome.gen.LayersStack;
import com.parzivail.util.gen.biome.gen.system.BiomeSampler;

public class BiomeGenerator {
	private final BiomeSampler biomeSampler;

	public BiomeGenerator(long seed) {
		this.biomeSampler = LayersStack.create(seed).get(0);
	}

	public TerrainBiome getBiome(int x, int z) {
		return this.biomeSampler.get(x, z);
	}
}
