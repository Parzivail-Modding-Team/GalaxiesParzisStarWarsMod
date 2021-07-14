package com.parzivail.util.noise;

import com.parzivail.util.math.MathUtil;
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
		var n = new Vec2f((float)nx, (float)ny);
		var dis = 2d;
		for (float x = -1; x <= 1; x++)
		{
			for (float y = -1; y <= 1; y++)
			{
				var q = new Vec2f(x, y);
				var p = MathUtil.add(MathUtil.floor(n), q);
				var d = MathUtil.length(MathUtil.sub(MathUtil.add(R(p), q), MathUtil.fract(n)));

				if (dis <= d)
					continue;

				dis = d;
			}
		}
		return dis;
	}

	public double eval(double nx, double ny, double nz)
	{
		var n = new Vec3d((float)nx, (float)ny, (float)nz);
		var dis = 2d;
		for (float x = -1; x <= 1; x++)
		{
			for (float y = -1; y <= 1; y++)
			{
				for (float z = -1; z <= 1; z++)
				{
					var q = new Vec3d(x, y, z);
					var p = MathUtil.floor(n).add(q);
					var d = R(p).add(q).subtract(MathUtil.fract(n)).length();

					if (dis <= d)
						continue;

					dis = d;
				}
			}
		}
		return dis;
	}
}
