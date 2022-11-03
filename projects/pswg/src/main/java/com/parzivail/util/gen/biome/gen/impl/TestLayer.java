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
		return BiomeList.getId(TerrainBiomes.TEST_BIOME);
	}
}
