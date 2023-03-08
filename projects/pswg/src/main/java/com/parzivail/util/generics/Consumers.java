package com.parzivail.util.generics;

public class Consumers
{
	@SafeVarargs
	public static <T> void noop(T... value)
	{
	}

	@SafeVarargs
	public static <T> boolean never(T... value)
	{
		return false;
	}
}
