package com.parzivail.util.world;

import com.parzivail.util.common.InfiniteWorleyNoise;
import com.parzivail.util.common.OpenSimplexNoise;
import com.parzivail.util.math.MathUtil;

public class ProcNoise
{
	private OpenSimplexNoise _noise;
	private InfiniteWorleyNoise _worley;
	private long _seed;

	public ProcNoise(long seed)
	{
		_seed = seed;
		_noise = new OpenSimplexNoise(seed);
		_worley = new InfiniteWorleyNoise(seed);
	}

	public double noise(double x, double z)
	{
		return (_noise.eval(x, z) + 1) / 2;
	}

	public double rawnoise(double x, double z)
	{
		return _noise.eval(x, z);
	}

	public double noise3(double x, double y, double z)
	{
		return (_noise.eval(x, y, z) + 1) / 2;
	}

	public double rawnoise3(double x, double y, double z)
	{
		return _noise.eval(x, y, z);
	}

	public double noise4(double x, double y, double z, double w)
	{
		return (_noise.eval(x, y, z, w) + 1) / 2;
	}

	public double rawnoise4(double x, double y, double z, double w)
	{
		return _noise.eval(x, y, z, w);
	}

	public double worley(double x, double z)
	{
		return (_worley.eval(x, z) + 1) / 2;
	}

	public double rawworley(double x, double z)
	{
		return _worley.eval(x, z);
	}

	//	public double worley3(double x, double y, double z)
	//	{
	//		return (_worley.eval((float)x, (float)y, (float)z) + 1) / 2;
	//	}
	//
	//	public double rawworley3(double x, double y, double z)
	//	{
	//		return _worley.eval((float)x, (float)y, (float)z);
	//	}

	public double noiseDx(double x, double z)
	{
		double n = noise(x, z);
		final double d = 0.001;
		return noise(x, z + d) - n;
	}

	public double noiseDz(double x, double z)
	{
		double n = noise(x, z);
		final double d = 0.001;
		return noise(x + d, z) - n;
	}

	public double octNoise(double x, double z, int octaves)
	{
		double n = noise(x, z) / 2;
		if (octaves <= 1)
			return n;
		return n + octNoise((x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
	}

	public double octWorley(double x, double z, int octaves)
	{
		double n = worley(x, z) / 2;
		if (octaves <= 1)
			return n;
		return n + octWorley((x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
	}

	public double octInvWorley(double x, double z, int octaves)
	{
		double n = (1 - worley(x, z)) / 2;
		if (octaves <= 1)
			return n;
		return n + octInvWorley((x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
	}

	public double hashA(double x, double z)
	{
		return MathUtil.fract(Math.sin(MathUtil.seed(x - 173.37, _seed) * 7441.35 - 4113.21 * Math.cos(x * z) + MathUtil.seed(z - 1743.7, _seed) * 1727.93 * 1291.27) * 2853.85 + MathUtil.oneOverGoldenRatio);
	}

	public double hashB(double x, double z)
	{
		return MathUtil.fract(Math.cos(MathUtil.seed(z - 143.37, _seed) * 4113.21 - 2853.85 * Math.sin(x * z) + MathUtil.seed(x - 743.37, _seed) * 1291.27 * 1727.93) * 4113.21 + MathUtil.oneOverGoldenRatio);
	}

	public double swissTurbulence(double pX, double pY, int octaves, double lacunarity, double gain, double warp)
	{
		double sum = 0;
		double freq = 1;
		double amp = 1;
		double dSumX = 0;
		double dSumY = 0;

		for (int i = 0; i < octaves; i++)
		{
			double nX = noise((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000);
			double nY = noiseDx((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000);
			double nZ = noiseDz((pX + warp * dSumX) * freq + i * 1000, (pY + warp * dSumY) * freq + i * 1000);

			sum = sum + amp * (1 - Math.abs(nX));
			dSumX = dSumX + amp * nY * -nX;
			dSumY = dSumY + amp * nZ * -nX;
			freq = freq * lacunarity;
			amp = amp * gain * MathUtil.clamp(sum);
		}

		return sum;
	}
}
