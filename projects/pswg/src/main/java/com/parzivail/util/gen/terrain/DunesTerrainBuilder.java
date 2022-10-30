package com.parzivail.util.gen.terrain;

import com.parzivail.util.gen.noise.OctaveNoise;

import java.util.Random;

public class DunesTerrainBuilder extends TerrainBuilder
{
	private final OctaveNoise noise;

	public DunesTerrainBuilder() {
		long seed = 100;
		this.noise = new OctaveNoise(2, new Random(seed), 40.0, 12.0, 1.0, 2.0, 2.0);
	}

	@Override
	public double build(int x, int y, int z)
	{
		double n = 1 - Math.abs(noise.sample(x, z));

		// Desmos: \frac{-x}{2}+\left(2+2n\right)
		return (-y / 2.0) + (2 + (2 * n));
	}
}
