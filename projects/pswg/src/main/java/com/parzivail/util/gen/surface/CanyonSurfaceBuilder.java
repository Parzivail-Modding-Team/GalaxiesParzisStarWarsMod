package com.parzivail.util.gen.surface;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.gen.noise.OctaveNoise;
import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;

import java.util.Random;

public class CanyonSurfaceBuilder implements SurfaceBuilder
{
	private final TwoStateSurfaceBuilder stone;
	private final TwoStateSurfaceBuilder sand;
	private final OctaveNoise noise;

	public CanyonSurfaceBuilder()
	{
		this.stone = new TwoStateSurfaceBuilder(SwgBlocks.Stone.Canyon.block.getDefaultState(), 3, SwgBlocks.Stone.Canyon.block.getDefaultState(), 16);
		this.sand = new TwoStateSurfaceBuilder(SwgBlocks.Sand.Canyon.getDefaultState(), 3, SwgBlocks.Stone.Canyon.block.getDefaultState(), 16);


		long seed = 100;
		this.noise = new OctaveNoise(3, new Random(seed), 120, 80, 1.0, 2.0, 2.0);
	}

	@Override
	public void build(ChunkView chunk, int x, int z, int height, Random random, BlockState defaultBlock, BlockState defaultFluid)
	{
//		double noise = this.noise.sample(x, z);

		if (height > 75)
		{
			stone.build(chunk, x, z, height, random, defaultBlock, defaultFluid);
		}
		else
		{
			sand.build(chunk, x, z, height, random, defaultBlock, defaultFluid);
		}
	}
}
