package com.parzivail.util.math;

import java.util.Random;

public class MathUtil
{
	public static final double oneOverGoldenRatio = 0.61803398875;
	private static final Random _rand = new Random();

	public static double fract(double d)
	{
		return d - Math.floor(d);
	}

	public static double[] fract(double[] v)
	{
		for (int i = 0; i < v.length; i++)
			v[i] = fract(v[i]);
		return v;
	}

	public static double seed(double d, long seed)
	{
		return Double.longBitsToDouble(Double.doubleToLongBits(d) ^ seed);
	}

	public static double[] floor(double[] v)
	{
		for (int i = 0; i < v.length; i++)
			v[i] = Math.floor(v[i]);
		return v;
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
}
