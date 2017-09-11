package com.parzivail.util.common;

/**
 * Created by colby on 12/24/2016.
 */
public class Join<T>
{
	private final T[] elements;

	public Join(T[] elements)
	{
		this.elements = elements;
	}

	public String with(String delim)
	{
		StringBuilder builder = new StringBuilder();

		for (T i : elements)
		{
			if (builder.length() != 0)
				builder.append(delim);
			builder.append(i);
		}

		return builder.toString();
	}
}
