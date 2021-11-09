package com.parzivail.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CollisionUtil
{
	private static final double EPSILON = 1e-9;

	public record ClosestPoints(Vec3d a, Vec3d b, double s, double t, double squareDistance)
	{
	}

	public static ClosestPoints closestPointsTriangleSegment(Vec3d p, Vec3d q, Vec3d a, Vec3d b, Vec3d c)
	{
		var pqVsAb = closestPointsOnSegments(p, q, a, b);
		var pqVsBc = closestPointsOnSegments(p, q, b, c);
		var pqVsCa = closestPointsOnSegments(p, q, c, a);

		var pVsAbcPt = closestPointOnTriangle(p, a, b, c);
		var pVsAbc = new ClosestPoints(p, pVsAbcPt, 0, 0, pVsAbcPt.squaredDistanceTo(p));

		var qVsAbcPt = closestPointOnTriangle(q, a, b, c);
		var qVsAbc = new ClosestPoints(q, qVsAbcPt, 0, 0, qVsAbcPt.squaredDistanceTo(q));

		var min = pqVsAb;

		if (pqVsBc.squareDistance < min.squareDistance)
			min = pqVsBc;

		if (pqVsCa.squareDistance < min.squareDistance)
			min = pqVsCa;

		if (pVsAbc.squareDistance < min.squareDistance)
			min = pVsAbc;

		if (qVsAbc.squareDistance < min.squareDistance)
			min = qVsAbc;

		return min;
	}

	// From Real-Time Collision Detection by Christer Ericson
	public static Vec3d closestPointOnTriangle(Vec3d p, Vec3d a, Vec3d b, Vec3d c)
	{
		var ab = b.subtract(a);
		var ac = c.subtract(a);
		var ap = p.subtract(a);

		var d1 = ab.dotProduct(ap);
		var d2 = ac.dotProduct(ap);

		if (d1 <= 0 && d2 <= 0)
			return a;

		var bp = p.subtract(b);
		var d3 = ab.dotProduct(bp);
		var d4 = ac.dotProduct(bp);

		if (d3 >= 0 && d4 <= d3)
			return b;

		var vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0)
		{
			var v = d1 / (d1 - d3);
			return a.add(ab.multiply(v));
		}

		var cp = p.subtract(c);
		var d5 = ab.dotProduct(cp);
		var d6 = ac.dotProduct(cp);
		if (d6 >= 0 && d5 <= d6) return c;

		var vb = d5 * d2 - d1 * d6;

		if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f)
		{
			var w = d2 / (d2 - d6);
			return a.add(ac.multiply(w));
		}

		var va = d3 * d6 - d5 * d4;

		if (va <= 0.0f && (d4 - d3) >= 0.0f && (d5 - d6) >= 0.0f)
		{
			var w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return b.add(c.subtract(b).multiply(w));
		}

		var denom = 1.0f / (va + vb + vc);

		var v = vb * denom;
		var w = vc * denom;
		return a.add(ab.multiply(v)).add(ac.multiply(w));
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
