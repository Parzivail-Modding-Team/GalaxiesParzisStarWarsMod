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
}
