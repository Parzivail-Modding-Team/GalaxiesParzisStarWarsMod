package com.parzivail.util.math;

public class Ease
{
	public static float linear(float t)
	{
		return t;
	}

	public static float inQuad(float t)
	{
		return t * t;
	}

	public static float outQuad(float t)
	{
		return -1 * t * (t - 2);
	}

	public static float inOutQuad(float t)
	{
		t *= 2;
		if (t < 1)
			return 1 / 2f * t * t;
		t--;
		return -1 / 2f * (t * (t - 2) - 1);
	}

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
			return 0.5f * t * t * t;
		t -= 2;
		return 0.5f * (t * t * t + 2);
	}

	public static float inExpo(float t)
	{
		return (float)Math.pow(2, 10 * (t - 1));
	}

	public static float outExpo(float t)
	{
		return (float)(-Math.pow(2, -10 * t) + 1);
	}

	public static float inOutExpo(float t)
	{
		t *= 2;
		if (t < 1)
			return (float)(0.5f * Math.pow(2, 10 * (t - 1)));
		t--;
		return (float)(0.5f * (-Math.pow(2, -10 * t) + 2));
	}

	public static float inCirc(float t)
	{
		return (float)(-1 * (Math.sqrt(1 - t * t) - 1));
	}

	public static float outCirc(float t)
	{
		t--;
		return (float)Math.sqrt(1 - t * t);
	}

	public static float inOutCirc(float t)
	{
		t *= 2;
		if (t < 1)
			return (float)(-0.5f * (Math.sqrt(1 - t * t) - 1));
		t -= 2;
		return (float)(0.5f * (Math.sqrt(1 - t * t) + 1));
	}
}
