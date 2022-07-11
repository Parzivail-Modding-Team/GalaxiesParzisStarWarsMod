package com.parzivail.util.world.biome;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface BiomeEvaluationCallback
{
	RegistryKey<Biome> getBiomeAt(double x, double z);
}
