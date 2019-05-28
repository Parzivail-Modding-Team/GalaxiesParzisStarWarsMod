package com.parzivail.util.debug;

public class Assert
{
	public static void isTrue(boolean query)
	{
		if (!query)
			throw new AssertionError();
	}

	public static void isFalse(boolean query)
	{
		if (query)
			throw new AssertionError();
	}
}
