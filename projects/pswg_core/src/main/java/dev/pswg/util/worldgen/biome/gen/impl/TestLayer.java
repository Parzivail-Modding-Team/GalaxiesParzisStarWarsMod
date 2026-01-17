package dev.pswg.util.worldgen.biome.gen.impl;

import dev.pswg.util.worldgen.biome.BiomeList;
import dev.pswg.util.worldgen.biome.TerrainBiomes;
import dev.pswg.util.worldgen.biome.gen.system.InitLayer;
import dev.pswg.util.worldgen.biome.gen.system.LayerSampleContext;

public class TestLayer implements InitLayer
{
	@Override
	public int sample(LayerSampleContext<?> context, int x, int z)
	{
		//TODO: update with the other biomes
		/*if (context.nextInt(2) == 0) {
			return BiomeList.getId(TerrainBiomes.TATOOINE_CANYON);
		}

		return context.nextInt(2) == 0 ? BiomeList.getId(TerrainBiomes.TATOOINE_DUNE_SEA) : BiomeList.getId(TerrainBiomes.TATOOINE_SALT_FLATS);*/
		return BiomeList.getId(TerrainBiomes.TATOOINE_CANYON);
	}
}
