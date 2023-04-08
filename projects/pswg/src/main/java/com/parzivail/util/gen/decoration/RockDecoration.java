package com.parzivail.util.gen.decoration;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.gen.noise.OctaveNoise;
import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public record RockDecoration(BlockState state, Block test) implements Decoration
{

	@Override
	public boolean generate(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos)
	{
		if (world.getBlockState(pos.down()).getBlock() != test)
		{
			return false;
		}
		OctaveNoise noise = new OctaveNoise(1, new Random(world.getSeed()), 6.0, 6.0, 1.0, 2.0, 2.0);

		int rad = 2;
		for (int x = -rad; x <= rad; x++)
		{
			for (int z = -rad; z <= rad; z++)
			{
				for (int y = -rad; y <= rad; y++)
				{
					double dx = x / (double) rad;
					double dy = y / (double) rad;
					double dz = z / (double) rad;

					double dist = Math.sqrt(dx * dx + dy * dy + dz * dz) + (noise.sample(x, y, z) * 0.2);

					if (dist > 1)
					{
						continue;
					}

					world.setBlockState(pos.add(x, y, z), state);
				}
			}
		}

		return false;
	}
}
