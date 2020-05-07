package com.parzivail.util.noise;

import com.parzivail.pswg.util.MathUtil;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

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

	private float R(double n)
	{
		return MathUtil.fract(Math.cos(MathUtil.seed(n * 89.42, _seed)) * 343.42);
	}

	private Vec2f R(Vec2f n)
	{
		return new Vec2f(R(n.x * 23.62f - 300 + n.y * 34.35f), R(n.x * 45.13f + 256 + n.y * 38.89f));
	}

	private Vec3d R(Vec3d n)
	{
		return new Vec3d(R(n.x * 23.62f - 300 + n.y * 34.35f + 663 + n.z * 36.57f), R(n.x * 45.13f + 256 + n.y * 38.89f - 764 + n.z * 91.58f), R(n.x * 13.2f - 458 + n.y * 56.24f + 172 + n.z * 68.45f));
	}

	public double eval(double nx, double ny)
	{
		Vec2f n = new Vec2f((float)nx, (float)ny);
		double dis = 2d;
		for (float x = -1; x <= 1; x++)
		{
			for (float y = -1; y <= 1; y++)
			{
				Vec2f q = new Vec2f(x, y);
				Vec2f p = MathUtil.add(MathUtil.floor(n), q);
				double d = MathUtil.length(MathUtil.sub(MathUtil.add(R(p), q), MathUtil.fract(n)));

				if (dis <= d)
					continue;

				dis = d;
			}
		}
		return dis;
	}

	public double eval(double nx, double ny, double nz)
	{
		Vec3d n = new Vec3d((float)nx, (float)ny, (float)nz);
		double dis = 2d;
		for (float x = -1; x <= 1; x++)
		{
			for (float y = -1; y <= 1; y++)
			{
				for (float z = -1; z <= 1; z++)
				{
					Vec3d q = new Vec3d(x, y, z);
					Vec3d p = MathUtil.floor(n).add(q);
					double d = R(p).add(q).subtract(MathUtil.fract(n)).length();

					if (dis <= d)
						continue;

					dis = d;
				}
			}
		}
		return dis;
	}
}
