package com.parzivail.swg.dimension.hoth.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainHothCrags implements ITerrainHeightmap
{
	private final ProcNoise _noise = new ProcNoise(0);

	public TerrainHothCrags()
	{
	}

	@Override
	public double getHeightAt(int x, int z)
	{
		return (1 - _noise.octWorley(x / 100f, z / 100f, 3)) * 100;
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
