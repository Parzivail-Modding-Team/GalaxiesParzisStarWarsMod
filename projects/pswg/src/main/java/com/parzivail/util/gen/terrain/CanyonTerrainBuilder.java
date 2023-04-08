package com.parzivail.util.gen.terrain;

import com.parzivail.util.gen.noise.OctaveNoise;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class CanyonTerrainBuilder extends TerrainBuilder
{
	private final OctaveNoise noise;
//	private final OctaveNoise wx;
//	private final OctaveNoise wz;
	private final OctaveNoise noiseMini;

	public CanyonTerrainBuilder()
	{
		long seed = 100;
		this.noise = new OctaveNoise(3, new Random(seed), 64.0, 16.0, 1.0, 2.0, 2.0);
//		this.wz = new OctaveNoise(1, new Random(seed), 64.0, 16.0, 1.0, 2.0, 2.0);
//		this.noise = new OctaveNoise(1, new Random(seed), 64.0, 16.0, 1.0, 2.0, 2.0);
		this.noiseMini = new OctaveNoise(2, new Random(seed), 8.0, 8.0, 1.0, 2.0, 2.0);
	}

	@Override
	public double build(int x, int y, int z)
	{
		double wx = Math.abs(noise.sample(x, 0, z));
		if (wx > 0.08)
		{
			wx *= 6; // TODO: noise!
		}

		wx = MathHelper.clamp(wx, 0, 1);

		// TODO: add a bit of variance
		return (-y + 9) + (wx * 4.5) + (noiseMini.sample(x, y, z) * 0.35);
	}
}