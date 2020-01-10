package com.parzivail.util.animation;

public class Animatable
{
	public final String name;
	public final int length;

	public Animatable(String name, int length)
	{
		this.name = name;
		this.length = length;
	}

	public void tick(float t)
	{
	}

	public void reset()
	{
	}
}
