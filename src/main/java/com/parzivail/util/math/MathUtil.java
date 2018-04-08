package com.parzivail.util.math;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MathUtil
{
	public static final double oneOverGoldenRatio = 0.61803398875;

	public static double fract(double d)
	{
		return d - Math.floor(d);
	}

	public static Vector2f fract(Vector2f v)
	{
		v.x = (float)fract(v.x);
		v.y = (float)fract(v.y);
		return v;
	}

	public static Vector3f fract(Vector3f v)
	{
		v.x = (float)fract(v.x);
		v.y = (float)fract(v.y);
		v.z = (float)fract(v.z);
		return v;
	}

	public static double seed(double d, long seed)
	{
		return Double.longBitsToDouble(Double.doubleToLongBits(d) ^ seed);
	}

	public static Vector2f floor(Vector2f v)
	{
		v.x = (float)Math.floor(v.x);
		v.y = (float)Math.floor(v.y);
		return v;
	}

	public static Vector3f floor(Vector3f v)
	{
		v.x = (float)Math.floor(v.x);
		v.y = (float)Math.floor(v.y);
		v.z = (float)Math.floor(v.z);
		return v;
	}
}
