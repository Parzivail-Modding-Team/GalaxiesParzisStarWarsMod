package dev.pswg.util.worldgen.biome;

import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.util.worldgen.decoration.ConfiguredDecoration;
import dev.pswg.util.worldgen.decoration.PatchDecoration;
import dev.pswg.util.worldgen.decoration.RockDecoration;
import dev.pswg.util.worldgen.decorator.ChanceHeightmapDecorator;
import dev.pswg.util.worldgen.decorator.CountHeightmapDecorator;
import dev.pswg.util.worldgen.surface.CanyonSurfaceBuilder;
import dev.pswg.util.worldgen.surface.SaltFlatsSurfaceBuilder;
import dev.pswg.util.worldgen.surface.TwoStateSurfaceBuilder;
import dev.pswg.util.worldgen.terrain.*;
import java.util.List;
import net.minecraft.world.level.biome.Biomes;

public class TerrainBiomes
{
	public static final TerrainBiome TATOOINE_CRAGGY_DUNES = new TerrainBiome(
			Biomes.PLAINS,
			new TwoStateSurfaceBuilder(GalaxiesBlocks.DESERT_SAND.defaultBlockState(), 3, GalaxiesBlocks.SMOOTH_DESERT_SANDSTONE.defaultBlockState(), 16),
			new CraggyDunesTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new CountHeightmapDecorator(3),
							new PatchDecoration(GalaxiesBlocks.FUNNEL_FLOWER.defaultBlockState(), 5, 8, true, List.of(GalaxiesBlocks.DESERT_SAND))
					)
			)
	);

	public static final TerrainBiome TATOOINE_SOFT_DUNES = new TerrainBiome(
			Biomes.TAIGA,
			new TwoStateSurfaceBuilder(GalaxiesBlocks.DESERT_SAND.defaultBlockState(), 3, GalaxiesBlocks.SMOOTH_DESERT_SANDSTONE.defaultBlockState(), 16),
			new SoftDunesTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new CountHeightmapDecorator(3),
							new PatchDecoration(GalaxiesBlocks.FUNNEL_FLOWER.defaultBlockState(), 5, 8, true, List.of(GalaxiesBlocks.DESERT_SAND))
					)
			)
	);
	public static final TerrainBiome TATOOINE_SALT_FLATS = new TerrainBiome(
			Biomes.FOREST,
			new SaltFlatsSurfaceBuilder(),
			new SaltFlatsTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new ChanceHeightmapDecorator(6),
							new PatchDecoration(GalaxiesBlocks.HKAK_BUSH.defaultBlockState(), 5, 8, true, List.of(GalaxiesBlocks.CAKED_SALT, GalaxiesBlocks.CANYON_SAND))
					),
					ConfiguredDecoration.of(
							new ChanceHeightmapDecorator(12),
							new RockDecoration(GalaxiesBlocks.CAKED_SALT.defaultBlockState(), GalaxiesBlocks.CAKED_SALT)
					)
			)
	);

	public static final TerrainBiome TATOOINE_DUNE_SEA = new TerrainBiome(
			Biomes.BIRCH_FOREST,
			new TwoStateSurfaceBuilder(GalaxiesBlocks.DESERT_SAND.defaultBlockState(), 3, GalaxiesBlocks.SMOOTH_DESERT_SANDSTONE.defaultBlockState(), 16),
			new DunesTerrainBuilder(),
			List.of(
					ConfiguredDecoration.of(
							new ChanceHeightmapDecorator(6),
							new PatchDecoration(GalaxiesBlocks.TUBER_STALK.defaultBlockState(), 5, 8, true, List.of(GalaxiesBlocks.DESERT_SAND))
					)
			)
	);

	public static final TerrainBiome TATOOINE_CANYON = new TerrainBiome(
			Biomes.TAIGA,
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