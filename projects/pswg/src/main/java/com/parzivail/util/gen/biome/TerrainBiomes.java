package com.parzivail.util.gen.biome;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.gen.decoration.ConfiguredDecoration;
import com.parzivail.util.gen.decoration.PatchDecoration;
import com.parzivail.util.gen.decorator.CountHeightmapDecorator;
import com.parzivail.util.gen.surface.DefaultSurfaceBuilder;
import com.parzivail.util.gen.terrain.BlobbyHillsTerrainBuilder;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;

public class TerrainBiomes
{
	public static final TerrainBiome TATOOINE_DUNES = new TerrainBiome(
			BiomeKeys.TAIGA,
			new DefaultSurfaceBuilder(SwgBlocks.Sand.Desert.getDefaultState(), SwgBlocks.Sand.Desert.getDefaultState()),
			new BlobbyHillsTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new CountHeightmapDecorator(3),
							new PatchDecoration(SwgBlocks.Plant.FunnelFlower.getDefaultState(), 5, 8, true, List.of(SwgBlocks.Sand.Desert))
					)
			)
	);

	public static void init() {
		BiomeList.register(TATOOINE_DUNES);
	}
}
