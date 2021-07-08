package com.parzivail.util.world.biome;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public class LinkedBiomeWeightMap
{
	private final int biome;
	private double[] weights;
	private final LinkedBiomeWeightMap next;

	public LinkedBiomeWeightMap(int biome, LinkedBiomeWeightMap next)
	{
		this.biome = biome;
		this.next = next;
	}

	public LinkedBiomeWeightMap(int biome, int chunkColumnCount, LinkedBiomeWeightMap next)
	{
		this.biome = biome;
		this.weights = new double[chunkColumnCount];
		this.next = next;
	}

	public int getBiome()
	{
		return biome;
	}

	public double[] getWeights()
	{
		return weights;
	}

	public void setWeights(double[] weights)
	{
		this.weights = weights;
	}

	public LinkedBiomeWeightMap getNext()
	{
		return next;
	}
}
