package com.parzivail.util.math;

public class RenderedValue
{
	private float value;
	private float prevTickValue;

	public RenderedValue()
	{
	}

	public RenderedValue(float initialValue)
	{
		value = initialValue;
	}

	public void set(float value)
	{
		this.value = value;
	}

	public float get(float partialTicks)
	{
		return prevTickValue + (value - prevTickValue) * partialTicks;
	}

	public void add(float x)
	{
		set(value + x);
	}

	public void tick()
	{
		prevTickValue = value;
	}
}
