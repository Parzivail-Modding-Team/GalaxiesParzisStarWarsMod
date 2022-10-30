package com.parzivail.util.gen.biome.gen.system;

import com.parzivail.util.gen.biome.BiomeList;
import com.parzivail.util.gen.biome.TerrainBiome;

import java.util.List;

public final class BiomeSampler
{
    private final CachingLayerSampler sampler;

    public BiomeSampler(CachingLayerSampler sampler) {
        this.sampler = sampler;
    }

    public TerrainBiome get(int x, int z) {
        return BiomeList.get(this.sampler.sample(x, z));
    }
}
