package com.parzivail.util.math;

public class Ease
{
	public static float inCubic(float t)
	{
		return t * t * t;
	}

	public static float outCubic(float t)
	{
		t--;
		return (t * t * t + 1);
	}

	public static float inOutCubic(float t)
	{
		t *= 2;
		if (t < 1)
			return t * t * t / 2;
		t -= 2;
		return (t * t * t + 2) / 2;
	}
}
