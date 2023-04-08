package com.parzivail.util.gen.terrain;

import com.parzivail.util.gen.noise.OctaveNoise;

import java.util.Random;

public class DunesTerrainBuilder extends TerrainBuilder
{
	private final OctaveNoise noise;
	private final OctaveNoise dx;
	private final OctaveNoise dz;
	private final OctaveNoise heightNoise;

	public DunesTerrainBuilder() {
		long seed = 100;
		Random random = new Random(seed);
		this.noise = new OctaveNoise(2, random, 40.0, 12.0, 1.0, 2.0, 2.0);
		this.heightNoise = new OctaveNoise(2, random, 40.0, 20.0, 1.0, 2.0, 2.0);
		this.dx = new OctaveNoise(1, random, 20.0, 12.0, 1.0, 2.0, 2.0);
		this.dz = new OctaveNoise(1, random, 20.0, 12.0, 1.0, 2.0, 2.0);
	}

	@Override
	public double build(int x, int y, int z)
	{
		double rx = dx.sample(x, z) * 4;
		double rz = dz.sample(x, z) * 4;
		double raw = noise.sample(x + rx, z + rz);
		double n = 1 - Math.sqrt(Math.abs(raw) + 0.08);

		double height = heightNoise.sample(x, y, z) * 0.5;

		// Desmos: \frac{-x}{2}+\left(2+2n\right)
		return (-y / 2.0) + ((height + 4) + (2 * n));
	}
}
