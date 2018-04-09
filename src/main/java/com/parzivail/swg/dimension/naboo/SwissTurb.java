package com.parzivail.swg.dimension.naboo;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;

public class SwissTurb implements ITerrainHeightmap
{
	private ProcNoise pNoise;

	public SwissTurb(long seed)
	{
		pNoise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(int x, int z)
	{
		return swissTurbulence(x / 300f, z / 300f, 4, 3, 0.5, 0.25) * 100;
	}

	private double swissTurbulence(double pX, double pY, int octaves, double lacunarity, double gain, double warp)
	{
		double sum = 0;
		double freq = 1;
		double amp = 1;
		double dSumX = 0;
		double dSumY = 0;

		for (int i = 0; i < octaves; i++)
		{
			double nX = pNoise.noise((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000);
			double nY = pNoise.noiseDx((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000);
			double nZ = pNoise.noiseDz((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000);

			sum = sum + amp * (1 - Math.abs(nX));
			dSumX = dSumX + amp * nY * -nX;
			dSumY = dSumY + amp * nZ * -nX;
			freq = freq * lacunarity;
			amp = amp * gain * clamp(sum);
		}

		return sum;
	}

	private double clamp(double x)
	{
		if (x > 1)
			return 1;
		if (x < 0)
			return 0;
		return x;
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
