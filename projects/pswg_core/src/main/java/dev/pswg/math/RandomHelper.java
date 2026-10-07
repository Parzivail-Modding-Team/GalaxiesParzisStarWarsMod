package dev.pswg.math;

import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * Utilities related to randomness
 */
public final class RandomHelper
{
	/**
	 * Samples a unit direction uniformly in the solid angle of a cone around a nonzero axis.
	 */
	public static Vec3 directionInCone(RandomSource random, Vec3 direction, float halfAngleDegrees)
	{
		var axis = direction.normalize();
		if (halfAngleDegrees <= 0)
			return axis;

		var right = axis.cross(Math.abs(axis.y) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0)).normalize();
		var up = right.cross(axis);
		var cosine = 1 - random.nextDouble() * (1 - Math.cos(Math.toRadians(halfAngleDegrees)));
		var sine = Math.sqrt(Math.max(0, 1 - cosine * cosine));
		var azimuth = random.nextDouble() * Math.PI * 2;

		return axis.scale(cosine).add(right.scale(sine * Math.cos(azimuth))).add(up.scale(sine * Math.sin(azimuth)));
	}

	/**
	 * Returns a random value from the given array
	 *
	 * @param random The random number generator to use
	 * @param values The array of values to choose from
	 *
	 * @return A random value from the given array
	 */
	public static <T> T oneOf(RandomSource random, T[] values)
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
	public static <T> T oneOf(RandomSource random, List<T> values)
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
	public static double nextGaussian(RandomSource random, double mean, double std)
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
	public static float floatBetween(RandomSource random, float min, float max)
	{
		return min + random.nextFloat() * (max - min);
	}
}
