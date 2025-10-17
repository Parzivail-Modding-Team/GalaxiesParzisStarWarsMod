package dev.pswg.math;

import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Utilities related to randomness
 */
public final class RandomHelper
{
	/**
	 * Returns a random value from the given array
	 *
	 * @param random The random number generator to use
	 * @param values The array of values to choose from
	 *
	 * @return A random value from the given array
	 */
	public static <T> T oneOf(Random random, T[] values)
	{
		return values[random.nextInt(values.length)];
	}

	/**
	 * Returns a random value from the given array
	 *
	 * @param random The random number generator to use
	 * @param values The array of values to choose from
	 *
	 * @return A random value from the given array
	 */
	public static <T> T oneOf(Random random, List<T> values)
	{
		return values.get(random.nextInt(values.size()));
	}

	/**
	 * Generates a random value from a normal distribution with the
	 * given mean and standard deviation
	 *
	 * @param random The random number generator to use
	 * @param mean   The mean of the distribution
	 * @param std    The standard deviation of the distribution
	 *
	 * @return A random value from the distribution
	 */
	public static double nextGaussian(Random random, double mean, double std)
	{
		var normalizedGaussian = random.nextGaussian();
		return normalizedGaussian * std + mean;
	}

	/**
	 * Returns a uniform float between the given min and max
	 *
	 * @param random The random number generator to use
	 * @param min    The minimum value to return
	 * @param max    The maximum value to return
	 *
	 * @return A uniform float between the given min and max
	 */
	public static float floatBetween(Random random, float min, float max)
	{
		return min + random.nextFloat() * (max - min);
	}
}
