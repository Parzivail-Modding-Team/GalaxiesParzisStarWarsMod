package com.parzivail.util.common;

import java.util.ArrayDeque;
import java.util.Queue;

public final class Pool<T>
{
	private final Queue<T> objects;

	public Pool()
	{
		this.objects = new ArrayDeque<>();
	}

	public T borrow()
	{
		return this.objects.remove();
	}

	public boolean any()
	{
		return this.objects.size() > 0;
	}

	public void giveBack(T object)
	{
		this.objects.add(object);
	}
}
