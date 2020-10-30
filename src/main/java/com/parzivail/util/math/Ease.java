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
}
