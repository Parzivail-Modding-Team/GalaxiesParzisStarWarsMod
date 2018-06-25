package com.parzivail.util.math;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class MathUtil
{
	private static final Random _rand = new Random();

	public static final double oneOverGoldenRatio = 0.61803398875;

	public static double fract(double d)
	{
		return d - Math.floor(d);
	}

	public static Vector2f fract(Vector2f v)
	{
		return new Vector2f((float)fract(v.x), (float)fract(v.y));
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
		return new Vector2f((float)Math.floor(v.x), (float)Math.floor(v.y));
	}

	public static Vector3f floor(Vector3f v)
	{
		v.x = (float)Math.floor(v.x);
		v.y = (float)Math.floor(v.y);
		v.z = (float)Math.floor(v.z);
		return v;
	}

	/**
	 * Gets a random value from an array
	 *
	 * @param array The array to pull items from
	 * @param <T>   The inferred type of value to operate with
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
}
