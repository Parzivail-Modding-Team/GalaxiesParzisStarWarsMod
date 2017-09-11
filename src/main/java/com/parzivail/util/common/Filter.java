package com.parzivail.util.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by colby on 12/18/2016.
 */
public class Filter<T>
{
	private ArrayList<T> objects;

	public Filter(T[] objects)
	{
		this.objects = new ArrayList<T>(Arrays.asList(objects));
	}

	public Filter(List<T> objects)
	{
		this.objects = new ArrayList<T>(objects);
	}

	public ArrayList<T> filter(Function<T, Boolean> keepFilter)
	{
		ArrayList<T> nl = (ArrayList<T>)objects.clone();
		nl.removeIf(obj -> !keepFilter.apply(obj));
		return nl;
	}
}
