package com.parzivail.util.data;

import java.util.HashMap;

public class NamedBufferUtil
{
	private static final HashMap<String, float[]> floatBuffers = new HashMap<>();
	private static final HashMap<String, int[]> intBuffers = new HashMap<>();

	public static float[] getF(String name, float... init)
	{
		if (!floatBuffers.containsKey(name))
			floatBuffers.put(name, init);
		return floatBuffers.get(name);
	}

	public static int[] getI(String name, int... init)
	{
		if (!intBuffers.containsKey(name))
			intBuffers.put(name, init);
		return intBuffers.get(name);
	}
}
