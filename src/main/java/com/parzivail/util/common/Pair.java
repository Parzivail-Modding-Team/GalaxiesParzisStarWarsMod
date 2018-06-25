package com.parzivail.util.common;

/**
 * Created by colby on 12/25/2017.
 */
public class Pair<T, T1>
{
	public T left;
	public T1 right;

	public Pair(T left, T1 right)
	{
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Pair<?, ?> pair = (Pair<?, ?>)o;

		return (left != null ? left.equals(pair.left) : pair.left == null) && (right != null ? right.equals(pair.right) : pair.right == null);
	}

	@Override
	public int hashCode()
	{
		int result = left != null ? left.hashCode() : 0;
		result = 31 * result + (right != null ? right.hashCode() : 0);
		return result;
	}
}
