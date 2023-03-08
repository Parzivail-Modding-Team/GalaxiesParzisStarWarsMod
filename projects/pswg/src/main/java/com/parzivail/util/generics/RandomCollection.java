package com.parzivail.util.generics;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<T>
{
	private final NavigableMap<Double, T> map = new TreeMap<>();
	private final Random random;
	private double total = 0;

	public RandomCollection(Random random)
	{
		this.random = random;
	}

	public RandomCollection<T> add(double weight, T result)
	{
		if (weight <= 0)
			return this;
		total += weight;
		map.put(total, result);
		return this;
	}

	public T sample()
	{
		var value = random.nextDouble() * total;
		return map.higherEntry(value).getValue();
	}
}
