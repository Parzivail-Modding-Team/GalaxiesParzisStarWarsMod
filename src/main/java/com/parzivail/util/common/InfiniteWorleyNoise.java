package com.parzivail.util.common;

import com.parzivail.util.math.MathUtil;
import net.minecraft.util.MathHelper;

public class InfiniteWorleyNoise
{
	private final long _seed;

	public InfiniteWorleyNoise(long seed)
	{
		_seed = seed;
	}

	public InfiniteWorleyNoise()
	{
		this(0);
	}

	private double r(double n)
	{
		return MathUtil.fract(MathHelper.cos((float)MathUtil.seed(n * 89.42, _seed)) * 343.42);
	}

	public double eval(double nx, double ny)
	{
		double dis = 2;
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				double px = Math.floor(nx) + x;
				double py = Math.floor(ny) + y;

				double dx = r(px * 23.62f - 300 + py * 34.35f) + x - MathUtil.fract(nx);
				double dy = r(px * 45.13f + 256 + py * 38.89f) + y - MathUtil.fract(ny);

				double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

				if (dis <= d)
					continue;

				dis = d;
			}
		}
		return dis;
	}

	public double eval(double nx, double ny, double nz)
	{
		double dis = 2;
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					double px = Math.floor(nx) + x;
					double py = Math.floor(ny) + y;
					double pz = Math.floor(nz) + z;

					double dx = r(px * 23.62f - 300 + py * 34.35f + 663 + pz * 36.57f) + x - MathUtil.fract(nx);
					double dy = r(px * 45.13f + 256 + py * 38.89f - 764 + pz * 91.58f) + y - MathUtil.fract(ny);
					double dz = r(px * 13.2f - 458 + py * 56.24f + 172 + pz * 68.45f) + z - MathUtil.fract(ny);

					double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));

					if (dis <= d)
						continue;

					dis = d;
				}
			}
		}
		return dis;
	}
}
