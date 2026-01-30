package dev.pswg.util.worldgen.surface;

import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.util.worldgen.noise.OctaveNoise;
import dev.pswg.util.worldgen.world.ChunkView;
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
		this.salt = new TwoStateSurfaceBuilder(GalaxiesBlocks.CAKED_SALT.getDefaultState(), 3, GalaxiesBlocks.SMOOTH_DESERT_SANDSTONE.getDefaultState(), 16);
		this.desert = new TwoStateSurfaceBuilder(GalaxiesBlocks.CANYON_SAND.getDefaultState(), 3, GalaxiesBlocks.SMOOTH_DESERT_SANDSTONE.getDefaultState(), 16);

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
