package com.parzivail.pswg.world.tatooine.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainTatooineWastes implements ITerrainHeightmap
{
	private final ProcNoise _noise;

	public TerrainTatooineWastes(long seed)
	{
		_noise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		double dx = _noise.noise(x / 5, z / 5 + 3000) * 10;
		double dz = _noise.noise(x / 5 + 3000, z / 5) * 10;

		double peaks = _noise.octaveInvWorley((x + dx) / 100 - 3000, (z + dz) / 100, 3) * 1.2;
		double noise = Math.max(peaks * 50, 35) - 30;

		noise *= 5 * (_noise.noise(x / 70, z / 70) + 0.1);

		noise += _noise.octaveNoise(x / 50, z / 50 - 3000, 3) * 30;

		return noise;
	}

	@Override
	public double getBiomeLerpAmount(int x, int z)
	{
		return 1;
	}

	@Override
	public double[] getBiomeWeightsAt(int x, int z)
	{
		return new double[] { 1 };
	}
}
