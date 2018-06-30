package com.parzivail.util.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by colby on 12/27/2016.
 */
public class CompositeTerrain implements ITerrainHeightmap
{
	private final List<TerrainLayer> layers;

	public CompositeTerrain()
	{
		layers = new ArrayList<>();
	}

	public CompositeTerrain(TerrainLayer... composite)
	{
		layers = Arrays.asList(composite);
	}

	public void addLayer(TerrainLayer layer)
	{
		layers.add(layer);
	}

	public double getHeightAt(int x, int z)
	{
		double value = 0;
		for (TerrainLayer terrainLayer : layers)
		{
			double tval = terrainLayer.GetValue(x, z);
			switch (terrainLayer.method)
			{
				case Add:
					value += tval;
					break;
				case Subtract:
					value -= tval;
					break;
				case Multiply:
					value *= tval;
					break;
			}
		}
		return value;
	}

	@Override
	public double getBiomeLerpAmount(int x, int z)
	{
		return 1;
	}

	@Override
	public double[] getBiomeWeightsAt(int x, int z)
	{
		return new double[] { 1 };
	}
}
