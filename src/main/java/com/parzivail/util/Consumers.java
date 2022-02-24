package com.parzivail.util;

public class Consumers
{
	public static void noop()
	{
	}

	public static <T> void noop(T value)
	{
	}

	public static <T> boolean never(T value)
	{
		return false;
	}
}
