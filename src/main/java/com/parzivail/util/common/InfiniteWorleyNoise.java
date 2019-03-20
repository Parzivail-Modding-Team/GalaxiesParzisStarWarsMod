package com.parzivail.util.common;

import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.math.lwjgl.Vector3f;

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

	private Vector2f R(Vector2f n)
	{
		return new Vector2f(R(n.x * 23.62f - 300 + n.y * 34.35f), R(n.x * 45.13f + 256 + n.y * 38.89f));
	}

	private Vector3f R(Vector3f n)
	{
		return new Vector3f(R(n.x * 23.62f - 300 + n.y * 34.35f + 663 + n.z * 36.57f), R(n.x * 45.13f + 256 + n.y * 38.89f - 764 + n.z * 91.58f), R(n.x * 13.2f - 458 + n.y * 56.24f + 172 + n.z * 68.45f));
	}

	public double eval(double nx, double ny)
	{
		Vector2f n = new Vector2f((float)nx, (float)ny);
		double dis = 2d;
		for (float x = -1; x <= 1; x++)
		{
			for (float y = -1; y <= 1; y++)
			{
				Vector2f q = new Vector2f(x, y);
				Vector2f p = Vector2f.add(MathUtil.floor(n), q, null);
				double d = (Vector2f.sub(Vector2f.add(R(p), q, null), MathUtil.fract(n), null)).length();

				if (dis <= d)
					continue;

				dis = d;
			}
		}
		return dis;
	}

	public double eval(double nx, double ny, double nz)
	{
		Vector3f n = new Vector3f((float)nx, (float)ny, (float)nz);
		double dis = 2d;
		for (float x = -1; x <= 1; x++)
		{
			for (float y = -1; y <= 1; y++)
			{
				for (float z = -1; z <= 1; z++)
				{
					Vector3f q = new Vector3f(x, y, z);
					Vector3f p = Vector3f.add(MathUtil.floor(n), q, null);
					double d = (Vector3f.sub(Vector3f.add(R(p), q, null), MathUtil.fract(n), null)).length();

					if (dis <= d)
						continue;

					dis = d;
				}
			}
		}
		return dis;
	}
}
