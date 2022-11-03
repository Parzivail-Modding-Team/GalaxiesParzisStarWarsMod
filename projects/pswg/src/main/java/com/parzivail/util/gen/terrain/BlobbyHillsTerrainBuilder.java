package com.parzivail.util.gen.terrain;

import com.parzivail.util.gen.noise.OctaveNoise;

import java.util.Random;

public class BlobbyHillsTerrainBuilder extends TerrainBuilder
{
	private final OctaveNoise noise;

	public BlobbyHillsTerrainBuilder() {
		long seed = 100;
		this.noise = new OctaveNoise(3, new Random(seed), 16.0, 12.0, 1.0, 2.0, 2.0);
	}

	@Override
	public double build(int x, int y, int z)
	{
		// Desmos, normal: \frac{-x+8}{2}
		// Desmos, hilly: \frac{-(x*1.6)+9}{4}

		var localNoiseX = noise.sample(x / 5f, -y / 5f, z / 5f) * 4;
		var localNoiseZ = noise.sample(x / 5f, y / 5f, z / 5f) * 4;

		return (noise.sample(x - y * localNoiseX, y, z - y * localNoiseZ)) + (-y + 8) / 2f;
	}
}
