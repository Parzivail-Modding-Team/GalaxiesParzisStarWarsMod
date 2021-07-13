package com.parzivail.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DecoratorUtil
{
	public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.HEIGHTMAP_SPREAD_DOUBLE.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
	public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_SPREAD_DOUBLE = HEIGHTMAP_SPREAD_DOUBLE.spreadHorizontally();
	public static final RangeDecoratorConfig BOTTOM_TO_TOP = new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.getBottom(), YOffset.getTop()));

	public static ConfiguredFeature<?, ?> random(int tries, int chance, UniformIntProvider repeat, Consumer<DataPool.Builder<BlockState>> poolBuilder)
	{
		var pool = pool();
		poolBuilder.accept(pool);
		return Feature.RANDOM_PATCH.configure(
				new RandomPatchFeatureConfig.Builder(new WeightedBlockStateProvider(pool), SimpleBlockPlacer.INSTANCE)
						.tries(tries)
						.cannotProject()
						.build()
		).decorate(HEIGHTMAP_SPREAD_DOUBLE).applyChance(chance).repeat(repeat);
	}

	public static DataPool.Builder<BlockState> pool()
	{
		return DataPool.builder();
	}

	public static ConfiguredFeature<?, ?> ore(int minY, int maxY, int clumpSize, int chunkQuantity, BlockState inStone, BlockState inDeepslate)
	{
		var targets = new ArrayList<OreFeatureConfig.Target>();
		if (inStone != null)
			targets.add(OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, inStone));
		if (inDeepslate != null)
			targets.add(OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, inDeepslate));

		return Feature.ORE.configure(new OreFeatureConfig(targets, clumpSize))
		                  .uniformRange(YOffset.fixed(minY), YOffset.fixed(maxY))
		                  .spreadHorizontally()
		                  .repeat(chunkQuantity);
	}
}
