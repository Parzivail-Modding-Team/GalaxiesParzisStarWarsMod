package com.parzivail.swg.dimension.hoth.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class TerrainHothCrags implements ITerrainHeightmap
{
	private final ProcNoise _noise = new ProcNoise(0);
	private final int octaves;

	public TerrainHothCrags(int octaves)
	{
		this.octaves = octaves;
	}

	@Override
	public double getHeightAt(int x, int z)
	{
		return _noise.octNoise(x / 150f, z / 150f, octaves) * 80;
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
