package com.parzivail.pswg.dimension.tatooine;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class TatooineBiome extends Biome
{
	public TatooineBiome()
	{
		super(new Biome.Settings()
				      .configureSurfaceBuilder(SurfaceBuilder.NOPE, new TernarySurfaceConfig(SwgBlocks.Sand.Tatooine.getDefaultState(), Blocks.STONE.getDefaultState(), Blocks.GRAVEL.getDefaultState()))
				      .precipitation(Biome.Precipitation.NONE)
				      .category(Biome.Category.DESERT)
				      .depth(0.125F)
				      .scale(0.05F)
				      .temperature(2.0F)
				      .downfall(0.0F)
				      .waterColor(0x3f76e4)
				      .waterFogColor(0x050533)
				      .parent(null));

		this.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.COAL_ORE.getDefaultState(), 17))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 128))));
		this.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.IRON_ORE.getDefaultState(), 9))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 64))));
		this.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.GOLD_ORE.getDefaultState(), 9))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(2, 0, 0, 32))));
		this.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DIAMOND_ORE.getDefaultState(), 8))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(1, 0, 0, 16))));
	}
}
