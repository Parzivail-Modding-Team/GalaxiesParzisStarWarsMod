package com.parzivail.util.math;

import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.math.lwjgl.Vector3f;

import java.util.Random;

public class MathUtil
{
	public static final double oneOverGoldenRatio = 0.61803398875;
	private static final Random _rand = new Random();

	public static float fract(double d)
	{
		return (float)(d - Math.floor(d));
	}

	public static Vector2f fract(Vector2f v)
	{
		return new Vector2f(fract(v.x), fract(v.y));
	}

	public static Vector3f fract(Vector3f v)
	{
		return new Vector3f(fract(v.x), fract(v.y), fract(v.z));
	}

	public static Vector3f lerp(float t, Vector3f a, Vector3f b)
	{
		return new Vector3f(lerp(t, a.x, b.x), lerp(t, a.y, b.y), lerp(t, a.z, b.z));
	}

	public static float lerp(float t, float a, float b)
	{
		return t * b + (1 - t) * a;
	}

	public static double seed(double d, long seed)
	{
		return Double.longBitsToDouble(Double.doubleToLongBits(d) ^ seed);
	}

	public static Vector2f floor(Vector2f v)
	{
		return new Vector2f((float)Math.floor(v.x), (float)Math.floor(v.y));
	}

	public static Vector3f floor(Vector3f v)
	{
		return new Vector3f((float)Math.floor(v.x), (float)Math.floor(v.y), (float)Math.floor(v.z));
	}

	public static double clamp(double x)
	{
		if (x > 1)
			return 1;
		if (x < 0)
			return 0;
		return x;
	}

	/**
	 * Gets a random value from an array
	 *
	 * @param array The array to pull items from
	 * @param <T>   The inferred type of value to operate with
	 *
	 * @return A random element of the supplied array
	 */
	public static <T> T getRandomElement(T[] array)
	{
		return array[_rand.nextInt(array.length)];
	}

	/**
	 * Gets a random primitive int value from an array
	 *
	 * @param array The array to pull items from
	 *
	 * @return A random element of the supplied array
	 */
	public static int getRandomElement(int[] array)
	{
		return array[_rand.nextInt(array.length)];
	}

	/**
	 * Returns a random boolean with a one-in-n chance of being true
	 *
	 * @param n The chance
	 *
	 * @return The random boolean
	 */
	public static boolean oneIn(int n)
	{
		return _rand.nextInt(n) == 0;
	}

	public static float roundToNearest(float x, float nearest)
	{
		return Math.round(x / nearest) * nearest;
	}

	public static float interpolateRotation(float from, float to, float p)
	{
		float f3;

		for (f3 = to - from; f3 < -180.0F; f3 += 360.0F)
			;

		while (f3 >= 180.0F)
			f3 -= 360.0F;

		return from + p * f3;
	}
}
