package com.parzivail.util.gen.surface;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.gen.noise.OctaveNoise;
import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class SaltFlatsSurfaceBuilder implements SurfaceBuilder
{
	private final TwoStateSurfaceBuilder salt;
	private final TwoStateSurfaceBuilder desert;
	private final OctaveNoise noise;

	public SaltFlatsSurfaceBuilder()
	{
		this.salt = new TwoStateSurfaceBuilder(SwgBlocks.Salt.Caked.getDefaultState(), 3, SwgBlocks.Sandstone.SmoothDesert.getDefaultState(), 16);
		this.desert = new TwoStateSurfaceBuilder(SwgBlocks.Sand.Canyon.getDefaultState(), 3, SwgBlocks.Sandstone.SmoothDesert.getDefaultState(), 16);


		long seed = 100;
		this.noise = new OctaveNoise(2, new Random(seed), 120, 80, 1.0, 2.0, 2.0);
	}

	@Override
	public void build(ChunkView chunk, int x, int z, int height, Random random, BlockState defaultBlock, BlockState defaultFluid)
	{
		double noise = this.noise.sample(x, z);

		if ((noise + random.nextDouble() * 0.04) > 0.2)
		{
			salt.build(chunk, x, z, height, random, defaultBlock, defaultFluid);
		}
		else
		{
			desert.build(chunk, x, z, height, random, defaultBlock, defaultFluid);
		}
	}
}
