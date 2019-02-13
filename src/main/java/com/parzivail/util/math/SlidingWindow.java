package com.parzivail.util.math;

public class SlidingWindow
{
	private final int length;
	private final float[] readings;

	private int readIndex;
	private float total;
	private float average;

	public SlidingWindow(int length)
	{
		this.length = length;
		readings = new float[length];
		for (int i = 0; i < readings.length; i++)
			readings[i] = 0;
	}

	public float getAverage()
	{
		return average;
	}

	public float slide(float nextValue)
	{
		total = total - readings[readIndex];
		// read from the sensor:
		readings[readIndex] = nextValue;
		// add the reading to the total:
		total = total + readings[readIndex];
		// advance to the next position in the array:
		readIndex = readIndex + 1;

		// if we're at the end of the array...
		if (readIndex >= length)
		{
			// ...wrap around to the beginning:
			readIndex = 0;
		}

		// calculate the average:
		average = total / length;
		return average;
	}
}
