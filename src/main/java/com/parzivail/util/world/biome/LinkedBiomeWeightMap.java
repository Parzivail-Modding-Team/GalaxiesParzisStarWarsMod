package com.parzivail.util.world.biome;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public class LinkedBiomeWeightMap
{
	private final RegistryKey<Biome> biome;
	private double[] weights;
	private final LinkedBiomeWeightMap next;

	public LinkedBiomeWeightMap(RegistryKey<Biome> biome, LinkedBiomeWeightMap next)
	{
		this.biome = biome;
		this.next = next;
	}

	public LinkedBiomeWeightMap(RegistryKey<Biome> biome, int chunkColumnCount, LinkedBiomeWeightMap next)
	{
		this.biome = biome;
		this.weights = new double[chunkColumnCount];
		this.next = next;
	}

	public RegistryKey<Biome> getBiome()
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
