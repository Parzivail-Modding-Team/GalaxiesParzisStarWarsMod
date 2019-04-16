package com.parzivail.util.world;

import net.minecraft.util.math.MathHelper;

/**
 * Created by colby on 12/28/2016.
 */
public class MultiCompositeTerrain implements ITerrainHeightmap
{
	private final ITerrainHeightmap[] terrains;
	private final float n;
	private final TerrainLayer lerpNoise;

	public MultiCompositeTerrain(long seed, int noiseScale, ITerrainHeightmap... terrains)
	{
		lerpNoise = new TerrainLayer(seed, TerrainCompositeMethod.Add, noiseScale, 1);
		this.terrains = terrains;
		n = terrains.length - 1;
	}

	public double getHeightAt(double x, double z)
	{
		double l = getBiomeLerpAmount((int)x, (int)z);
		double y = 0;
		for (int i = 0; i < terrains.length; i++)
		{
			if ((i - 1f) / n <= l && l <= (i + 1f) / n)
				y += (-Math.abs(n * l - i) + 1) * terrains[i].getHeightAt(x, z);
		}
		return y;
	}

	@Override
	public double getBiomeLerpAmount(int x, int z)
	{
		return MathHelper.clamp(lerpNoise.GetValue(x, z), 0, 1);
	}

	@Override
	public double[] getBiomeWeightsAt(int x, int z)
	{
		double[] r = new double[terrains.length];
		double l = getBiomeLerpAmount(x, z);
		for (int i = 0; i < terrains.length; i++)
		{
			if ((i - 1f) / n <= l && l <= (i + 1f) / n)
				r[i] = (-Math.abs(n * l - i) + 1);
		}
		return r;
	}
}
