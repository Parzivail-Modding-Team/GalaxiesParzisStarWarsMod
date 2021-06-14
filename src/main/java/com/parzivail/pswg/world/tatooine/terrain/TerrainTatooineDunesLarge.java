package com.parzivail.pswg.world.tatooine.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainTatooineDunesLarge implements ITerrainHeightmap
{
	private final ProcNoise _noise;

	public TerrainTatooineDunesLarge(long seed)
	{
		_noise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		double noise = _noise.noise(x / 400 - 3000, z / 400) * 50;

		noise += _noise.noise(x / 50, z / 50 - 3000) * 25;

		noise *= (1 - Math.abs(_noise.rawNoise(x / 150, z / 150 + 3000))) * 1.8;

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
