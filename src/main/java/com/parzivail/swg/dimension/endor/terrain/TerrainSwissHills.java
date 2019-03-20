package com.parzivail.swg.dimension.endor.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainSwissHills implements ITerrainHeightmap
{
	private final ProcNoise pNoise;

	public TerrainSwissHills(long seed)
	{
		pNoise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		return pNoise.swissTurbulence(x / 200f, z / 200f, 4, 3, 0.5, 0.25) * 70;
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
