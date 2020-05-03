package com.parzivail.util.math;

import net.minecraft.util.math.MathHelper;

public class SlidingWindow
{
	private final float[] window;
	private int index;

	private float prevAverage;
	private float average;

	public SlidingWindow(int width)
	{
		window = new float[width];
	}

	public void push(float value)
	{
		window[index] = value;
		index++;
		index %= window.length;

		computeAverage();
	}

	private void computeAverage()
	{
		prevAverage = average;

		average = 0;

		for (float f : window)
			average += f;

		average /= window.length;
	}

	public float getAverage()
	{
		return average;
	}

	public float getAverage(float tickDelta)
	{
		return MathHelper.lerp(tickDelta, prevAverage, average);
	}
}
