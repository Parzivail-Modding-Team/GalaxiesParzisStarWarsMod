package com.parzivail.util.generics;

import java.util.ArrayList;
import java.util.function.Function;

public class Numbered<T> extends ArrayList<T>
{
	/**
	 * @param count     1 ... count
	 * @param generator
	 */
	public Numbered(int count, Function<Integer, T> generator)
	{
		for (var i = 1; i <= count; i++)
			add(generator.apply(i));
	}
}
