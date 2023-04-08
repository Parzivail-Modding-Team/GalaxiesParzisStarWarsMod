package com.parzivail.util.gen.terrain;

import com.parzivail.util.gen.noise.OctaveNoise;

import java.util.Random;

public class SaltFlatsTerrainBuilder extends TerrainBuilder
{
	private final OctaveNoise noise;

	public SaltFlatsTerrainBuilder() {
		long seed = 100;
		this.noise = new OctaveNoise(3, new Random(seed), 16.0, 8.0, 1.0, 2.0, 2.0);
	}

	@Override
	public double build(int x, int y, int z)
	{
		return (-y + 9) + (noise.sample(x, z) * 0.3);
	}
}
