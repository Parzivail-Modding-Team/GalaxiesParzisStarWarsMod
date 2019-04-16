package com.parzivail.util.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * Class to replicate the C# Linq functionality as concisely as possible
 *
 * @param <T> List content type
 */
public class Enumerable<T> extends ArrayList<T>
{
	private Enumerable()
	{
	}

	/**
	 * Creates an Enumerable from another collection of items
	 *
	 * @param l   The collection of items
	 * @param <T> List content type
	 *
	 * @return A new Enumerable
	 */
	public static <T> Enumerable<T> from(Collection<T> l)
	{
		Enumerable<T> e = new Enumerable<>();
		e.addAll(l);
		return e;
	}

	/**
	 * Creates an Enumerable from another collection of items
	 *
	 * @param l   The collection of items
	 * @param <T> List content type
	 *
	 * @return A new Enumerable
	 */
	public static <T> Enumerable<T> from(T[] l)
	{
		Enumerable<T> e = new Enumerable<>();
		e.addAll(Arrays.asList(l));
		return e;
	}

	/**
	 * Creates a new empty enumerable
	 *
	 * @param <T> List content type
	 *
	 * @return A new Enumerable
	 */
	public static <T> Enumerable<T> empty()
	{
		return new Enumerable<>();
	}

	public Enumerable<T> skip(int amount)
	{
		Enumerable<T> result = empty();
		for (int i = amount; i < size(); i++)
			result.add(get(i));
		return result;
	}

	/**
	 * Returns a list of the values selected (returned) by <code>f</code>
	 *
	 * @param f   The query
	 * @param <R> The type of the selected value
	 *
	 * @return A list of the selected values
	 */
	public <R> Enumerable<R> select(Function<T, R> f)
	{
		Enumerable<R> r = new Enumerable<>();
		for (T t : this)
			r.add(f.apply(t));
		return r;
	}

	/**
	 * Returns the first value that satisfies <code>f</code>
	 *
	 * @param f The query
	 *
	 * @return The first value that satisfies <code>f</code>
	 */
	public T first(Function<T, Boolean> f)
	{
		if (size() == 0)
			return null;
		for (T t : this)
			if (f.apply(t))
				return t;
		return null;
	}

	/**
	 * Returns the first value
	 *
	 * @return The first value
	 */
	public T first()
	{
		if (size() == 0)
			return null;
		return get(0);
	}

	/**
	 * Returns an composite list of the values selected (returned) by <code>f</code>
	 *
	 * @param f   The query
	 * @param <R> The type of the selected value
	 *
	 * @return A list of the selected values
	 */
	public <R> Enumerable<R> selectMany(Function<T, Collection<R>> f)
	{
		Enumerable<R> r = new Enumerable<>();
		for (T t : this)
			r.addAll(f.apply(t));
		return r;
	}

	/**
	 * Returns a list of only the items which satisfy <code>f</code>
	 *
	 * @param f The query
	 *
	 * @return A list of the filtered values
	 */
	public Enumerable<T> where(Function<T, Boolean> f)
	{
		Enumerable<T> r = new Enumerable<>();
		for (T t : this)
			if (f.apply(t))
				r.add(t);
		return r;
	}

	/**
	 * Query if any item within the list satisfies <code>f</code>
	 *
	 * @param f The query
	 *
	 * @return <code>true</code> of any item is satisfactory
	 */
	public boolean any(Function<T, Boolean> f)
	{
		for (T t : this)
			if (f.apply(t))
				return true;
		return false;
	}

	/**
	 * Query if all items within the list satisfy <code>f</code>
	 *
	 * @param f The query
	 *
	 * @return <code>true</code> of all items are satisfactory
	 */
	public boolean all(Function<T, Boolean> f)
	{
		for (T t : this)
			if (!f.apply(t))
				return false;
		return true;
	}

	/**
	 * Returns the maximum of a list of values
	 *
	 * @param f The query
	 *
	 * @return The max value of the list
	 */
	public float max(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		float max = f.apply(get(0));
		for (T t : this)
			max = Math.max(max, f.apply(t));
		return max;
	}

	/**
	 * Returns the minimum of a list of values
	 *
	 * @param f The query
	 *
	 * @return The min value of the list
	 */
	public float min(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		float min = f.apply(get(0));
		for (T t : this)
			min = Math.min(min, f.apply(t));
		return min;
	}

	/**
	 * Computes the aggregate value of the list according to <code>f</code>
	 * <p>
	 * It operates in such a way that if <code>aggregate((x, y) -> x + y)</code> is called
	 * on the list [1, 2, 3, 4, 5], the following groupings would occur: ((((1 + 2) + 3) + 4) + 5)
	 *
	 * @param f The query
	 *
	 * @return The aggregated value
	 */
	public T aggregate(DualFunction<T, T, T> f)
	{
		if (size() == 0)
			return null;
		T agg = get(0);
		for (int i = 1; i < size(); i++)
			agg = f.apply(agg, get(i));
		return agg;
	}

	/**
	 * Computes the sum of a list of values
	 *
	 * @param f The query
	 *
	 * @return The sum of the list
	 */
	public float sum(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		float sum = 0;
		for (T t : this)
			sum += f.apply(t);
		return sum;
	}

	/**
	 * Computes the average of a list of values
	 *
	 * @param f The query
	 *
	 * @return The average of the list
	 */
	public float average(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		return sum(f) / size();
	}

	@FunctionalInterface
	interface DualFunction<A, B, R>
	{
		R apply(A one, B two);
	}
}
