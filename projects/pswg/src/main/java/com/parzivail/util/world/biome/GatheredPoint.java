package com.parzivail.util.world.biome;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public class GatheredPoint<T>
{
	private final double x;
	private final double z;
	private final int hash;
	private T tag;

	public GatheredPoint(double x, double z, int hash)
	{
		this.x = x;
		this.z = z;
		this.hash = hash;
	}

	public double getX()
	{
		return x;
	}

	public double getZ()
	{
		return z;
	}

	public double getHash()
	{
		return hash;
	}

	public T getTag()
	{
		return tag;
	}

	public void setTag(T tag)
	{
		this.tag = tag;
	}
}
