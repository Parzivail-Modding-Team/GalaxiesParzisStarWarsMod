package com.parzivail.pswg.dimension.tatooine.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainTatooineHills implements ITerrainHeightmap
{
	private final ProcNoise _noise;

	public TerrainTatooineHills(long seed)
	{
		_noise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		double noise = _noise.noise(x / 100, z / 100 + 3000) * 20;

		noise *= (1 - Math.abs(_noise.rawNoise(x / 30 + 2000, z / 30)));

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
