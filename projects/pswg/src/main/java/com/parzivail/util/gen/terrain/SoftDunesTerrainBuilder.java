package com.parzivail.util.gen.terrain;

import com.parzivail.util.gen.noise.OctaveNoise;

import java.util.Random;

public class SoftDunesTerrainBuilder extends TerrainBuilder
{
	private final OctaveNoise noise;

	public SoftDunesTerrainBuilder()
	{
		long seed = 100;
		this.noise = new OctaveNoise(3, new Random(seed), 16.0, 12.0, 1.0, 2.0, 2.0);
	}

	@Override
	public double build(int x, int y, int z)
	{
		// Desmos, normal: \frac{-x+8}{2}
		// Desmos, hilly: \frac{-(x*1.6)+9}{4}

		var localNoiseX = noise.normalizedSample(x / 5f, y / 4f - 1000, z / 5f);
		var localNoiseZ = noise.normalizedSample(x / 5f, y / 4f - 2000, z / 5f);

		return (noise.sample(x - localNoiseX * 100, y, z - localNoiseZ * 100)) + (-y + 6) / 1.4f;
	}
}
