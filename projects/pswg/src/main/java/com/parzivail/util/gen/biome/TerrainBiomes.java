package com.parzivail.util.gen.biome;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.gen.decoration.ConfiguredDecoration;
import com.parzivail.util.gen.decoration.PatchDecoration;
import com.parzivail.util.gen.decoration.RockDecoration;
import com.parzivail.util.gen.decorator.ChanceHeightmapDecorator;
import com.parzivail.util.gen.decorator.CountHeightmapDecorator;
import com.parzivail.util.gen.surface.CanyonSurfaceBuilder;
import com.parzivail.util.gen.surface.SaltFlatsSurfaceBuilder;
import com.parzivail.util.gen.surface.TwoStateSurfaceBuilder;
import com.parzivail.util.gen.terrain.*;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;

public class TerrainBiomes
{
	public static final TerrainBiome TATOOINE_CRAGGY_DUNES = new TerrainBiome(
			BiomeKeys.PLAINS,
			new TwoStateSurfaceBuilder(SwgBlocks.Sand.Desert.getDefaultState(), 3, SwgBlocks.Sandstone.SmoothDesert.getDefaultState(), 16),
			new CraggyDunesTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new CountHeightmapDecorator(3),
							new PatchDecoration(SwgBlocks.Plant.FunnelFlower.getDefaultState(), 5, 8, true, List.of(SwgBlocks.Sand.Desert))
					)
			)
	);

	public static final TerrainBiome TATOOINE_SOFT_DUNES = new TerrainBiome(
			BiomeKeys.TAIGA,
			new TwoStateSurfaceBuilder(SwgBlocks.Sand.Desert.getDefaultState(), 3, SwgBlocks.Sandstone.SmoothDesert.getDefaultState(), 16),
			new SoftDunesTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new CountHeightmapDecorator(3),
							new PatchDecoration(SwgBlocks.Plant.FunnelFlower.getDefaultState(), 5, 8, true, List.of(SwgBlocks.Sand.Desert))
					)
			)
	);

	public static final TerrainBiome TATOOINE_SALT_FLATS = new TerrainBiome(
			BiomeKeys.FOREST,
			new SaltFlatsSurfaceBuilder(),
			new SaltFlatsTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new ChanceHeightmapDecorator(6),
							new PatchDecoration(SwgBlocks.Plant.HkakBush.getDefaultState(), 5, 8, true, List.of(SwgBlocks.Salt.Caked, SwgBlocks.Sand.Canyon))
					),
					ConfiguredDecoration.of(
							new ChanceHeightmapDecorator(12),
							new RockDecoration(SwgBlocks.Salt.Caked.getDefaultState(), SwgBlocks.Salt.Caked)
					)
			)
	);

	public static final TerrainBiome TATOOINE_DUNE_SEA = new TerrainBiome(
			BiomeKeys.BIRCH_FOREST,
			new TwoStateSurfaceBuilder(SwgBlocks.Sand.Desert.getDefaultState(), 3, SwgBlocks.Sandstone.SmoothDesert.getDefaultState(), 16),
			new DunesTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new ChanceHeightmapDecorator(6),
							new PatchDecoration(SwgBlocks.Plant.Tuber.getDefaultState(), 5, 8, true, List.of(SwgBlocks.Sand.Desert))
					)
			)
	);

	public static final TerrainBiome TATOOINE_CANYON = new TerrainBiome(
			BiomeKeys.TAIGA,
			new CanyonSurfaceBuilder(),
			new CanyonTerrainBuilder(),
			List.of(
			)
	);

	public static void init()
	{
		BiomeList.register(TATOOINE_CRAGGY_DUNES);
		BiomeList.register(TATOOINE_SOFT_DUNES);
		BiomeList.register(TATOOINE_SALT_FLATS);
		BiomeList.register(TATOOINE_DUNE_SEA);
		BiomeList.register(TATOOINE_CANYON);
	}
}
