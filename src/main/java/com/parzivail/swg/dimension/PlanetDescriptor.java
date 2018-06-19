package com.parzivail.swg.dimension;

public class PlanetDescriptor
{
	public final String name;
	public final float rotationPeriod;
	public final int diameter;
	public final float gravity;

	public PlanetDescriptor(String name, float rotationPeriod, int diameter, float gravity)
	{
		this.name = name;
		this.rotationPeriod = rotationPeriod;
		this.diameter = diameter;
		this.gravity = gravity;
	}
}
