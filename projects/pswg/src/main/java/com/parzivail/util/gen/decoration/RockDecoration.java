package com.parzivail.util.gen.decoration;

import com.parzivail.util.gen.noise.OctaveNoise;
import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public class RockDecoration extends Decoration
{
	@Override
	public boolean generate(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos)
	{
		OctaveNoise noise = new OctaveNoise(1, new Random(world.getSeed()), 6.0, 6.0, 1.0, 2.0, 2.0);

		for (int x = -4; x <= 4; x++)
		{
			for (int z = -4; z <= 4; z++)
			{
				for (int y = -4; y <= 4; y++)
				{
					double dx = x / 4.0;
					double dy = y / 4.0;
					double dz = z / 4.0;

					double dist = Math.sqrt(dx * dx + dy * dy + dz * dz) + (noise.sample(x, y, z) * 0.2);

					if (dist > 1)
					{
						continue;
					}

					world.setBlockState(pos.add(x, y, z), Blocks.DEEPSLATE.getDefaultState());
				}
			}
		}

		return false;
	}
}
