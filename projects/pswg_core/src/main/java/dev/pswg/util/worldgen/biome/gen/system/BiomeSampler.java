package dev.pswg.util.worldgen.biome.gen.system;

import dev.pswg.util.worldgen.biome.BiomeList;
import dev.pswg.util.worldgen.biome.TerrainBiome;

public final class BiomeSampler
{
	private final CachingLayerSampler sampler;

	public BiomeSampler(CachingLayerSampler sampler)
	{
		this.sampler = sampler;
	}

	public TerrainBiome get(int x, int z)
	{
		return BiomeList.get(this.sampler.sample(x, z));
	}
}