package com.parzivail.pswg.dimension.tatooine.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainTatooineDunesSmall implements ITerrainHeightmap
{
	private final ProcNoise _noise;

	public TerrainTatooineDunesSmall(long seed)
	{
		_noise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		double noise = _noise.noise(x / 400 - 3000, z / 400) * 25;

		noise += _noise.noise(x / 50, z / 50 - 3000) * 25;

		noise *= (1 - Math.abs(_noise.rawNoise(x / 70, z / 70 + 3000)));

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
