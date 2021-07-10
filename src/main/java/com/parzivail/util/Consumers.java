package com.parzivail.util;

import java.util.function.Consumer;

public class Consumers
{
	public static <T> Consumer<T> noop()
	{
		return a -> {};
	}
}
