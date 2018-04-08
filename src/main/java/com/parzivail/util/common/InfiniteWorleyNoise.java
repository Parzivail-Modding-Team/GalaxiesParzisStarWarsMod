package com.parzivail.util.common;

import com.parzivail.util.math.MathUtil;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

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

	public double eval(float nx, float ny)
	{
		return worley(new Vector2f(nx, ny));
	}

	public double eval(float nx, float ny, float nz)
	{
		return worley(new Vector3f(nx, ny, nz));
	}

	private float r(float n)
	{
		return (float)MathUtil.fract(Math.cos(MathUtil.seed(n * 89.42, _seed)) * 343.42);
	}

	private Vector2f r(Vector2f n)
	{
		return new Vector2f(r(n.x * 23.62f - 300 + n.y * 34.35f), r(n.x * 45.13f + 256 + n.y * 38.89f));
	}

	private Vector3f r(Vector3f n)
	{
		return new Vector3f(r(n.x * 23.62f - 300 + n.y * 34.35f + 663 + n.z * 36.57f), r(n.x * 45.13f + 256 + n.y * 38.89f - 764 + n.z * 91.58f), r(n.x * 13.2f - 458 + n.y * 56.24f + 172 + n.z * 68.45f));
	}

	private double worley(Vector2f n)
	{
		double dis = 2d;
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				Vector2f q = new Vector2f(x, y);
				Vector2f p = Vector2f.add(MathUtil.floor(n), q, null);
				double d = Vector2f.sub(Vector2f.add(r(p), q, null), MathUtil.fract(n), null).length();

				if (!(dis > d))
					continue;

				dis = d;
			}
		}
		return dis;
	}

	private double worley(Vector3f n)
	{
		double dis = 2d;
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					Vector3f q = new Vector3f(x, y, z);
					Vector3f p = Vector3f.add(MathUtil.floor(n), q, null);
					double d = Vector3f.sub(Vector3f.add(r(p), q, null), MathUtil.fract(n), null).length();

					if (!(dis > d))
						continue;

					dis = d;
				}
			}
		}
		return dis;
	}
}
