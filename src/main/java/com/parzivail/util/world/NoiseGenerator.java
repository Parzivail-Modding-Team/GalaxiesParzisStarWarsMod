package com.parzivail.util.world;

@FunctionalInterface
public interface NoiseGenerator
{
	double generate(double x, double z);
}
