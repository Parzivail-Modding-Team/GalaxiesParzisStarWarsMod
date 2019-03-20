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
	public double getHeightAt(double x, double z)
	{
		double j = _noise.octaveNoise(x / 420, z / 420, 4);

		// the first 2 values essentially translate to the radius of the creaters, smaller the number the bigger the creater
		// var h = ProcNoise.Worley(x / 400 + 200, z / 400 + 200, j) * 1.45;
		double h1 = _noise.octaveNoise(x / 110, z / 110, 4);
		double h2 = _noise.octaveNoise(x / 110 + 999, z / 110 - 999, 4);

		// the first 2 values essentially translate to the radius of the creaters, smaller the number the bigger the creater
		double h = _noise.worley(x / 400 + h1, z / 400 - h1, j) * 1.3;
		if (h < 1)
			h = circularize(h);
		else
			h = 1;

		return h * 80 + _noise.octaveNoise(x / 70, z / 70, 5) * 20 + 60;
	}

	private double circularize(double x)
	{
		return 1 - Math.sqrt(1 - Math.pow(x, 1.8));
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
