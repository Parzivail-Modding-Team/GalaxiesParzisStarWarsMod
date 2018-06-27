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

	public double eval(double nx, double ny)
	{
		return worley(nx, ny);
	}

	//	public double eval(float nx, float ny, float nz)
	//	{
	//		return worley(new Vector3f(nx, ny, nz));
	//	}

	private double r(double n)
	{
		return MathUtil.fract(MathHelper.cos((float)MathUtil.seed(n * 89.42, _seed)) * 343.42);
	}

	//	private Vector2f r2(Vector2f n)
	//	{
	//		return new Vector2f(r(n.x * 23.62f - 300 + n.y * 34.35f), r(n.x * 45.13f + 256 + n.y * 38.89f));
	//	}
	//
	//	private Vector3f r3(Vector3f n)
	//	{
	//		return new Vector3f(r(n.x * 23.62f - 300 + n.y * 34.35f + 663 + n.z * 36.57f), r(n.x * 45.13f + 256 + n.y * 38.89f - 764 + n.z * 91.58f), r(n.x * 13.2f - 458 + n.y * 56.24f + 172 + n.z * 68.45f));
	//	}

	private double worley(double nx, double ny)
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

				double d = Math.pow(dx, 2) + Math.pow(dy, 2);

				if (dis <= d)
					continue;

				dis = d;
			}
		}
		return Math.sqrt(dis);
	}

	//	private double worley(Vector3f n)
	//	{
	//		double dis = 2d;
	//		for (int x = -1; x <= 1; x++)
	//		{
	//			for (int y = -1; y <= 1; y++)
	//			{
	//				for (int z = -1; z <= 1; z++)
	//				{
	//					Vector3f q = new Vector3f(x, y, z);
	//					Vector3f p = Vector3f.add(MathUtil.floor(n), q, null);
	//					double d = Vector3f.sub(Vector3f.add(r3(p), q, null), MathUtil.fract(n), null).length();
	//
	//					if (!(dis > d))
	//						continue;
	//
	//					dis = d;
	//				}
	//			}
	//		}
	//		return dis;
	//	}
}
