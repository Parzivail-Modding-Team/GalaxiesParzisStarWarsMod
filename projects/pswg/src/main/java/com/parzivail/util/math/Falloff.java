package com.parzivail.util.math;

import java.util.function.Function;

/**
 * See these functions in <a href="https://www.desmos.com/calculator/jy6mb1cwoi">Desmos</a>
 */
public class Falloff
{
	public static Function<Double, Double> linear()
	{
		return x -> 1 - x;
	}

	public static Function<Double, Double> power(double a)
	{
		return x -> 1 - Math.pow(x, a);
	}

	public static Function<Double, Double> cliff(double a)
	{
		return x -> 1 - Math.pow(x, a / x);
	}

	public static Function<Double, Double> sinusoidal(double a)
	{
		return x -> 1 - Math.pow(Math.sin((Math.PI * x) / 2), a);
	}

	public static Function<Double, Double> circular(double a)
	{
		return x -> Math.sqrt(1 - Math.pow(x, 2 * a));
	}
}
