package com.parzivail.util.world.biome;

@FunctionalInterface
public interface BiomeEvaluationCallback
{
	int getBiomeAt(double x, double z);
}
