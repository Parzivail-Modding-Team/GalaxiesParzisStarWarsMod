package com.parzivail.pswg.world.tatooine.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainTatooinePlains implements ITerrainHeightmap
{
	private final ProcNoise _noise;

	public TerrainTatooinePlains(long seed)
	{
		_noise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		return _noise.noise(x / 100, z / 100) * 10 + Math.abs(_noise.rawNoise(x / 50, z / 50)) * 5;
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
