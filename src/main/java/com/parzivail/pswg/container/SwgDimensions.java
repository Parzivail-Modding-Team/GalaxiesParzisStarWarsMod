package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.crop.HkakBushBlock;
import com.parzivail.pswg.world.tatooine.TatooineBiomeSource;
import com.parzivail.pswg.world.tatooine.TatooineChunkGenerator;
import com.parzivail.util.Consumers;
import com.parzivail.util.world.DecoratorUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.function.Consumer;

public class SwgDimensions
{
	private static final Consumer<SpawnSettings.Builder> SPAWN_NONE = Consumers.noop();
	private static final Consumer<GenerationSettings.Builder> GEN_NONE = Consumers.noop();

	private static int getSkyColor(float temperature)
	{
		float f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

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

		private static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_FUNNEL_FLOWER = DecoratorUtil.random(64, blockStateBuilder -> blockStateBuilder.add(SwgBlocks.Plant.BlossomingFunnelFlower.getDefaultState(), 4).add(SwgBlocks.Plant.FunnelFlower.getDefaultState(), 7));
		private static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_POONTEN_GRASS = DecoratorUtil.random(64, blockStateBuilder -> blockStateBuilder.add(SwgBlocks.Plant.PoontenGrass.getDefaultState(), 4).add(SwgBlocks.Plant.DriedPoontenGrass.getDefaultState(), 7));
		private static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_HKAK_BUSHES = DecoratorUtil.random(8, blockStateBuilder -> blockStateBuilder.add(SwgBlocks.Plant.HkakBush.getDefaultState(), 10).add(SwgBlocks.Plant.HkakBush.getDefaultState().with(HkakBushBlock.AGE,3), 1));

		private static final ConfiguredFeature<?, ?> ORE_TITANIUM = DecoratorUtil.ore(0, 12, 1, 1, SwgBlocks.Ore.Titanium.getDefaultState(), /* TODO */ null);
		private static final ConfiguredFeature<?, ?> ORE_DIATIUM = DecoratorUtil.ore(0, 32, 2, 3, SwgBlocks.Ore.Diatium.getDefaultState(), /* TODO */ null);
		private static final ConfiguredFeature<?, ?> ORE_ZERSIUM = DecoratorUtil.ore(0, 64, 4, 10, SwgBlocks.Ore.Zersium.getDefaultState(), /* TODO */ null);
		private static final ConfiguredFeature<?, ?> ORE_HELICITE = DecoratorUtil.ore(32, 128, 6, 25, SwgBlocks.Ore.Helicite.getDefaultState(), /* TODO */ null);
		private static final ConfiguredFeature<?, ?> ORE_THORILIDE = DecoratorUtil.ore(48, 96, 9, 4, SwgBlocks.Ore.Thorilide.getDefaultState(), /* TODO */ null);

		public static void register()
		{
			Registry.register(Registry.BIOME_SOURCE, Resources.id("tatooine_source"), TatooineBiomeSource.CODEC);
			Registry.register(Registry.CHUNK_GENERATOR, Resources.id("tatooine_generator"), TatooineChunkGenerator.CODEC);

			Registry.register(BuiltinRegistries.BIOME, BIOME_DUNES_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, GEN_NONE));

			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_patch_funnel_flower"), PATCH_FUNNEL_FLOWER);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_patch_poonten_grass"), PATCH_POONTEN_GRASS);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_patch_hkak_bushes"), PATCH_HKAK_BUSHES);

			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_titanium"), ORE_TITANIUM);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_diatium"), ORE_DIATIUM);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_zersium"), ORE_ZERSIUM);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_helicite"), ORE_HELICITE);
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_thorilide"), ORE_THORILIDE);

			Registry.register(BuiltinRegistries.BIOME, BIOME_CANYONS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
			}));

			Registry.register(BuiltinRegistries.BIOME, BIOME_PLATEAU_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
			}));

			Registry.register(BuiltinRegistries.BIOME, BIOME_MUSHMESA_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
			}));

			Registry.register(BuiltinRegistries.BIOME, BIOME_WASTES_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
			}));

			Registry.register(BuiltinRegistries.BIOME, BIOME_MOUNTAINS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
			}));

			Registry.register(BuiltinRegistries.BIOME, BIOME_SALTFLATS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
			}));

			Registry.register(BuiltinRegistries.BIOME, BIOME_OASIS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
			}));
		}

		private static void addVegetalPatch(GenerationSettings.Builder builder, ConfiguredFeature<RandomPatchFeatureConfig, ?> patch)
		{
			builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, patch.decorate(DecoratorUtil.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(8));
		}

		private static Biome getGenericDesertBiome(Consumer<SpawnSettings.Builder> spawnOptions, Consumer<GenerationSettings.Builder> generationOptions)
		{
			var surfaceBuilder = SurfaceBuilder.DEFAULT
					.withConfig(new TernarySurfaceConfig(
							SwgBlocks.Sand.Desert.getDefaultState(),
							Blocks.STONE.getDefaultState(),
							Blocks.GRAVEL.getDefaultState()));

			var spawnSettings = new SpawnSettings.Builder();
			spawnOptions.accept(spawnSettings);

			var genSettings = new GenerationSettings.Builder()
					.surfaceBuilder(surfaceBuilder);

			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIRT);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GRAVEL);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DEEPSLATE);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_CLAY);

			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_COAL);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_IRON);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GOLD);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_COPPER);

			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_TITANIUM);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_DIATIUM);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_ZERSIUM);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_HELICITE);
			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_THORILIDE);

			generationOptions.accept(genSettings);

			return new Biome.Builder()
					.precipitation(Biome.Precipitation.NONE)
					.category(Biome.Category.NONE)
					.depth(0)
					.scale(0)
					.temperature(2)
					.downfall(0)
					.effects(
							new BiomeEffects.Builder()
									.waterColor(0x3f76e4)
									.waterFogColor(0x050533)
									.fogColor(0xc0d8ff)
									.skyColor(getSkyColor(2.0F))
									.build()
					)
					.spawnSettings(spawnSettings.build())
					.generationSettings(genSettings.build())
					.build();
		}
	}
}
