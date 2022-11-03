package com.parzivail.util.gen.biome;

import com.parzivail.util.gen.decoration.ConfiguredDecoration;
import com.parzivail.util.gen.decoration.PatchDecoration;
import com.parzivail.util.gen.decorator.CountHeightmapDecorator;
import com.parzivail.util.gen.surface.DefaultSurfaceBuilder;
import com.parzivail.util.gen.terrain.BlobbyHillsTerrainBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;

public class TerrainBiomes
{
	public static final TerrainBiome TEST_BIOME = new TerrainBiome(
			BiomeKeys.TAIGA,
			new DefaultSurfaceBuilder(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState()),
			new BlobbyHillsTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new CountHeightmapDecorator(3),
							new PatchDecoration(Blocks.GRASS.getDefaultState(), 5, 8, true, List.of(Blocks.GRASS_BLOCK))
					)
			)
	);

	public static void init() {
	    BiomeList.register(TEST_BIOME);
	}
}
