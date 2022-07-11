package com.parzivail.util.world;

import com.parzivail.util.world.biome.BiomeSurfaceHint;

@FunctionalInterface
public interface NoiseGenerator
{
	BiomeSurfaceHint generate(double x, double z);
}
