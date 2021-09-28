package com.parzivail.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CollisionUtil
{
	private static final double EPSILON = 1e-9;

	public static record ClosestPoints(Vec3d a, Vec3d b, double s, double t, double squareDistance)
	{
	}

	// From Real-Time Collision Detection by Christer Ericson
	public static ClosestPoints closestPointsOnSegments(Vec3d p1, Vec3d q1, Vec3d p2, Vec3d q2)
	{
		var d1 = q1.subtract(p1);
		var d2 = q2.subtract(p2);
		var r = p1.subtract(p2);

		var a = d1.dotProduct(d1);
		var e = d2.dotProduct(d2);
		var f = d2.dotProduct(r);

		if (a <= EPSILON && e <= EPSILON)
		{
			var c1subc2 = p1.subtract(p2);
			return new ClosestPoints(p1, p2, 0, 0, c1subc2.dotProduct(c1subc2));
		}

		double s, t;

		if (a <= EPSILON)
		{
			s = 0;
			t = MathHelper.clamp(f / e, 0, 1);
		}
		else
		{
			var c = d1.dotProduct(r);
			if (e <= EPSILON)
			{
				t = 0;
				s = MathHelper.clamp(-c / a, 0, 1);
			}
			else
			{
				var b = d1.dotProduct(d2);
				var denom = a * e - b * b;

				if (denom != 0)
				{
					s = MathHelper.clamp((b * f - c * e) / denom, 0, 1);
				}
				else
					s = 0;

				var tnom = b * s + f;
				if (tnom < 0.0f)
				{
					t = 0;
					s = MathHelper.clamp(-c / a, 0, 1);
				}
				else if (tnom > e)
				{
					t = 1;
					s = MathHelper.clamp((b - c) / a, 0, 1);
				}
				else
					t = tnom / e;
			}
		}

		var c1 = p1.add(d1.multiply(s));
		var c2 = p2.add(d2.multiply(t));
		var c1subc2 = c1.subtract(c2);
		return new ClosestPoints(c1, c2, s, t, c1subc2.dotProduct(c1subc2));
	}
}
