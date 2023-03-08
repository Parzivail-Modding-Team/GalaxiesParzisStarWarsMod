package com.parzivail.util.generics;

import java.util.Collections;
import java.util.HashSet;

public class Make
{
	@SafeVarargs
	public static <T> HashSet<T> hashSet(T... items)
	{
		HashSet<T> set = new HashSet<T>(items.length);
		Collections.addAll(set, items);
		return set;
	}
}
