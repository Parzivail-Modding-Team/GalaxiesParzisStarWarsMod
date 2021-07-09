package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.world.tatooine.TatooineBiomeSource;
import com.parzivail.pswg.world.tatooine.TatooineChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class SwgDimensions
{
	public static class Tatooine
	{
		public static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(Registry.DIMENSION_KEY, Resources.id("tatooine"));
		public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(Registry.WORLD_KEY, DIMENSION_KEY.getValue());

		public static final RegistryKey<Biome> BIOME_DUNES_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_dunes"));
		public static final RegistryKey<Biome> BIOME_CANYONS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_canyons"));
		public static final RegistryKey<Biome> BIOME_PLATEAU_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_plateau"));
		public static final RegistryKey<Biome> BIOME_MUSHMESA_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_mushroom_mesa"));
		public static final RegistryKey<Biome> BIOME_WASTES_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_wastes"));
		public static final RegistryKey<Biome> BIOME_MOUNTAINS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_mountains"));
		public static final RegistryKey<Biome> BIOME_SALTFLATS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_salt_flats"));
		public static final RegistryKey<Biome> BIOME_OASIS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_oasis"));

		public static void register()
		{
			Registry.register(Registry.BIOME_SOURCE, Resources.id("tatooine_source"), TatooineBiomeSource.CODEC);
			Registry.register(Registry.CHUNK_GENERATOR, Resources.id("tatooine_generator"), TatooineChunkGenerator.CODEC);

			Registry.register(BuiltinRegistries.BIOME, BIOME_DUNES_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_CANYONS_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_PLATEAU_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_MUSHMESA_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_WASTES_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_MOUNTAINS_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_SALTFLATS_KEY.getValue(), getGenericDesertBiome());
			Registry.register(BuiltinRegistries.BIOME, BIOME_OASIS_KEY.getValue(), getGenericDesertBiome());
		}

		private static Biome getGenericDesertBiome()
		{
			var surfaceBuilder = SurfaceBuilder.DEFAULT
					.withConfig(new TernarySurfaceConfig(
							SwgBlocks.Sand.Desert.getDefaultState(),
							Blocks.SANDSTONE.getDefaultState(),
							Blocks.GRAVEL.getDefaultState()));

			return new Biome.Builder()
					.precipitation(Biome.Precipitation.NONE)
					.category(Biome.Category.NONE)
					.depth(0)
					.scale(0)
					.temperature(1)
					.downfall(0)
					.effects((new BiomeEffects.Builder())
							         .waterColor(0x3f76e4)
							         .waterFogColor(0x050533)
							         .fogColor(0xc0d8ff)
							         .skyColor(0x77adff)
							         .build())
					.spawnSettings(new SpawnSettings.Builder().build())
					.generationSettings(
							new GenerationSettings.Builder()
									.surfaceBuilder(surfaceBuilder)
									.build()
					)
					.build();
		}
	}
}
