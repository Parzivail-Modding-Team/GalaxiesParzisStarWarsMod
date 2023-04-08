package com.parzivail.util.gen.biome.gen.impl;

import com.parzivail.util.gen.biome.BiomeList;
import com.parzivail.util.gen.biome.TerrainBiomes;
import com.parzivail.util.gen.biome.gen.system.InitLayer;
import com.parzivail.util.gen.biome.gen.system.LayerSampleContext;

public class TestLayer implements InitLayer
{
	@Override
	public int sample(LayerSampleContext<?> context, int x, int z)
	{
		if (context.nextInt(2) == 0) {
			return BiomeList.getId(TerrainBiomes.TATOOINE_CANYON);
		}

		return context.nextInt(2) == 0 ? BiomeList.getId(TerrainBiomes.TATOOINE_DUNE_SEA) : BiomeList.getId(TerrainBiomes.TATOOINE_SALT_FLATS);
	}
}
