package com.parzivail.pswg.dimension.tatooine.terrain;

import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.ProcNoise;
import net.minecraft.util.math.MathHelper;

public class TerrainTatooineCanyons implements ITerrainHeightmap
{
	private final ProcNoise _noise;

	public TerrainTatooineCanyons(long seed)
	{
		_noise = new ProcNoise(seed);
	}

	@Override
	public double getHeightAt(double x, double z)
	{
		double s = 2;
		double h = get(x / s, z / s);
		double d = 5 * s;

		double blur = 0;
		blur = blur + get(x / s - d, z / s - d);
		blur = blur + get(x / s - d, z / s + d);

		blur = blur + get(x / s + d, z / s - d);
		blur = blur + get(x / s + d, z / s + d);
		blur = blur / 4;

		h = 1 - (h - blur);
		h = 1 - 1 / (2 * h) - 0.48;
		h = h * 35;

		h = MathHelper.clamp(h, 0, 1);
		double j = _noise.octaveNoise(x / 200f, z / 200f, 6) * 90;

		return (h * 0.8 + _noise.octaveNoise(x / 200f, z / 200f, 3) * 0.8) * (j + 10);
	}

	private double get(double x, double z)
	{
		double offsetX = _noise.noise(x / 10, z / 10 + 1000) / 10;
		double offsetY = _noise.noise(x / 10 + 1000, z / 10) / 10;

		return _noise.worley(x / 50 + offsetX, z / 50 + offsetY);
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
