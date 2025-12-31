package dev.pswg.structure;

import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ContainerLootCratesFeature extends Feature<DefaultFeatureConfig>
{
	public ContainerLootCratesFeature()
	{
		super(DefaultFeatureConfig.CODEC);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context)
	{
		return false;
	}
}