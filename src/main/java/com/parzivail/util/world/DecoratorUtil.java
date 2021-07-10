package com.parzivail.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.function.Consumer;

public class DecoratorUtil
{
	public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.HEIGHTMAP_SPREAD_DOUBLE.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
	public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_SPREAD_DOUBLE = HEIGHTMAP_SPREAD_DOUBLE.spreadHorizontally();

	public static ConfiguredFeature<RandomPatchFeatureConfig, ?> random(int tries, Consumer<DataPool.Builder<BlockState>> poolBuilder)
	{
		var pool = pool();
		poolBuilder.accept(pool);
		return Feature.RANDOM_PATCH.configure((new RandomPatchFeatureConfig.Builder(new WeightedBlockStateProvider(pool), SimpleBlockPlacer.INSTANCE)).tries(tries).cannotProject().build());
	}

	public static DataPool.Builder<BlockState> pool()
	{
		return DataPool.builder();
	}
}
