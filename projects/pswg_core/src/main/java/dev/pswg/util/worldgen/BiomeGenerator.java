package dev.pswg.util.worldgen;

import dev.pswg.util.worldgen.biome.TerrainBiome;
import dev.pswg.util.worldgen.biome.gen.LayersStack;
import dev.pswg.util.worldgen.biome.gen.system.BiomeSampler;

public class BiomeGenerator
{
	private final BiomeSampler biomeSampler;

	public BiomeGenerator(long seed)
	{
		this.biomeSampler = LayersStack.create(seed).get(0);
	}

	public TerrainBiome getBiome(int x, int z)
	{
		return this.biomeSampler.get(x, z);
	}
}
