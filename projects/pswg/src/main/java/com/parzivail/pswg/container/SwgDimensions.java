package com.parzivail.pswg.container;

import com.parzivail.util.Consumers;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

import java.util.function.Consumer;

public class SwgDimensions
{
	private static final Consumer<SpawnSettings.Builder> SPAWN_NONE = Consumers::noop;
	private static final Consumer<GenerationSettings.Builder> GEN_NONE = Consumers::noop;

	private static int getSkyColor(float temperature)
	{
		var f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

//	public static class Tatooine
//	{
//		public static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(Registry.DIMENSION_KEY, Resources.id("tatooine"));
//		public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(Registry.WORLD_KEY, DIMENSION_KEY.getValue());
//
//		public static final RegistryKey<Biome> BIOME_DUNES_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_dunes"));
//		public static final RegistryKey<Biome> BIOME_CANYONS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_canyons"));
//		public static final RegistryKey<Biome> BIOME_PLATEAU_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_plateau"));
//		public static final RegistryKey<Biome> BIOME_MUSHMESA_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_mushroom_mesa"));
//		public static final RegistryKey<Biome> BIOME_WASTES_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_wastes"));
//		public static final RegistryKey<Biome> BIOME_MOUNTAINS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_mountains"));
//		public static final RegistryKey<Biome> BIOME_SALTFLATS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_salt_flats"));
//		public static final RegistryKey<Biome> BIOME_OASIS_KEY = RegistryKey.of(Registry.BIOME_KEY, Resources.id("tatooine_oasis"));
//
//		private static final MesaMushroomFeature MESA_MUSHROOM = new MesaMushroomFeature(SingleStateFeatureConfig.CODEC);
//		private static final JaporTreeFeature JAPOR_TREE = new JaporTreeFeature(SingleStateFeatureConfig.CODEC);
//
//		private static final ConfiguredFeature<?, ?> PATCH_FUNNEL_FLOWER = DecoratorUtil.bigPatches(64, 1, UniformIntProvider.create(10, 20), blockStateBuilder -> blockStateBuilder.add(SwgBlocks.Plant.BlossomingFunnelFlower.getDefaultState(), 4).add(SwgBlocks.Plant.FunnelFlower.getDefaultState(), 7));
//		private static final ConfiguredFeature<?, ?> PATCH_POONTEN_GRASS = DecoratorUtil.bigPatches(64, 1, UniformIntProvider.create(10, 20), blockStateBuilder -> blockStateBuilder.add(SwgBlocks.Plant.PoontenGrass.getDefaultState(), 4).add(SwgBlocks.Plant.DriedPoontenGrass.getDefaultState(), 7));
//		private static final ConfiguredFeature<?, ?> PATCH_HKAK_BUSHES = DecoratorUtil.bigPatches(16, 1, UniformIntProvider.create(10, 20), blockStateBuilder -> blockStateBuilder.add(SwgBlocks.Plant.HkakBush.getDefaultState(), 10).add(SwgBlocks.Plant.HkakBush.getDefaultState().with(HkakBushBlock.AGE, 3), 1));
//
//		private static final ConfiguredFeature<?, ?> ORE_TITANIUM = DecoratorUtil.ore(0, 12, 1, 1, SwgBlocks.Ore.Titanium.getDefaultState(), /* TODO */ null);
//		private static final ConfiguredFeature<?, ?> ORE_DIATIUM = DecoratorUtil.ore(0, 32, 2, 3, SwgBlocks.Ore.Diatium.getDefaultState(), /* TODO */ null);
//		private static final ConfiguredFeature<?, ?> ORE_ZERSIUM = DecoratorUtil.ore(0, 64, 4, 10, SwgBlocks.Ore.Zersium.getDefaultState(), /* TODO */ null);
//		private static final ConfiguredFeature<?, ?> ORE_HELICITE = DecoratorUtil.ore(32, 128, 6, 25, SwgBlocks.Ore.Helicite.getDefaultState(), /* TODO */ null);
//		private static final ConfiguredFeature<?, ?> ORE_THORILIDE = DecoratorUtil.ore(48, 96, 9, 4, SwgBlocks.Ore.Thorilide.getDefaultState(), /* TODO */ null);
//
//		private static final ConfiguredFeature<?, ?> MESA_MUSHROOMS = MESA_MUSHROOM.configure(new SingleStateFeatureConfig(SwgBlocks.Stone.DesertSediment.getDefaultState())).decorate(DecoratorUtil.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(20);
//		private static final ConfiguredFeature<?, ?> OASIS_LAKES = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.WATER.getDefaultState())).range(DecoratorUtil.BOTTOM_TO_TOP).spreadHorizontally().applyChance(4);
//		private static final ConfiguredFeature<?, ?> JAPOR_TREES = JAPOR_TREE.configure(new SingleStateFeatureConfig(Blocks.AIR.getDefaultState())).decorate(DecoratorUtil.SQUARE_HEIGHTMAP_SPREAD_DOUBLE);
//
//		public static void register()
//		{
//			FIXED_SEED_REGISTRY.put(WORLD_KEY.getValue(), 0L);
//
//			Registry.register(Registry.BIOME_SOURCE, Resources.id("tatooine_source"), TatooineBiomeSource.CODEC);
//			Registry.register(Registry.CHUNK_GENERATOR, Resources.id("tatooine_generator"), TatooineChunkGenerator.CODEC);
//
//			Registry.register(Registry.FEATURE, Resources.id("mesa_mushroom"), MESA_MUSHROOM);
//			Registry.register(Registry.FEATURE, Resources.id("japor_tree"), JAPOR_TREE);
//
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_patch_funnel_flower"), PATCH_FUNNEL_FLOWER);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_patch_poonten_grass"), PATCH_POONTEN_GRASS);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_patch_hkak_bushes"), PATCH_HKAK_BUSHES);
//
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_titanium"), ORE_TITANIUM);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_diatium"), ORE_DIATIUM);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_zersium"), ORE_ZERSIUM);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_helicite"), ORE_HELICITE);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_ore_thorilide"), ORE_THORILIDE);
//
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_oasis_lakes"), OASIS_LAKES);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_mesa_mushrooms"), MESA_MUSHROOMS);
//			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Resources.id("tatooine_japor_trees"), JAPOR_TREES);
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_DUNES_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, GEN_NONE));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_CANYONS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
//				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
//			}));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_PLATEAU_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
//				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
//			}));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_MUSHMESA_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//				builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, MESA_MUSHROOMS);
//			}));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_WASTES_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
//				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
//			}));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_MOUNTAINS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
//			}));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_SALTFLATS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//			}));
//
//			Registry.register(BuiltinRegistries.BIOME, BIOME_OASIS_KEY.getValue(), getGenericDesertBiome(SPAWN_NONE, builder -> {
//				addVegetalPatch(builder, PATCH_FUNNEL_FLOWER);
//				addVegetalPatch(builder, PATCH_POONTEN_GRASS);
//				addVegetalPatch(builder, PATCH_HKAK_BUSHES);
//				builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, JAPOR_TREES);
//				builder.feature(GenerationStep.Feature.LAKES, OASIS_LAKES);
//			}));
//		}
//
//		private static void addVegetalPatch(GenerationSettings.Builder builder, ConfiguredFeature<?, ?> patch)
//		{
//			builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, patch.decorate(DecoratorUtil.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(8));
//		}
//
//		private static Biome getGenericDesertBiome(Consumer<SpawnSettings.Builder> spawnOptions, Consumer<GenerationSettings.Builder> generationOptions)
//		{
//			var spawnSettings = new SpawnSettings.Builder();
//			spawnOptions.accept(spawnSettings);
//
//			var genSettings = new GenerationSettings.Builder();
//
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIRT);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GRAVEL);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_BLACKSTONE);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_CLAY);
//
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_COAL_UPPER);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_IRON_UPPER);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GOLD);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_COPPER);
//
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_TITANIUM);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_DIATIUM);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_ZERSIUM);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_HELICITE);
//			genSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_THORILIDE);
//
//			generationOptions.accept(genSettings);
//
//			return new Biome.Builder()
//					.precipitation(Biome.Precipitation.NONE)
//					.category(Biome.Category.NONE)
//					.depth(0)
//					.scale(0)
//					.temperature(2)
//					.downfall(0)
//					.effects(
//							new BiomeEffects.Builder()
//									.waterColor(0x3f76e4)
//									.waterFogColor(0x050533)
//									.fogColor(0xc0d8ff)
//									.skyColor(getSkyColor(2.0F))
//									.build()
//					)
//					.spawnSettings(spawnSettings.build())
//					.generationSettings(genSettings.build())
//					.build();
//		}
//	}
}
