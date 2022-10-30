package com.parzivail.util.gen.decorator;

import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChanceHeightmapDecorator extends Decorator
{
	private final double chance;
	private final Heightmap.Type heightmap;

	// chance is 1 in chance
	public ChanceHeightmapDecorator(int chance)
	{
		this(1.0 / chance, Heightmap.Type.WORLD_SURFACE_WG);
	}

	// Raw chance
	public ChanceHeightmapDecorator(double chance, Heightmap.Type heightmap)
	{
		this.chance = chance;
		this.heightmap = heightmap;
	}

	@Override
	public List<BlockPos> findPositions(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos)
	{
		List<BlockPos> list = new ArrayList<>();

		if (random.nextDouble() < this.chance)
		{
			int x = random.nextInt(16) + pos.getX();
			int z = random.nextInt(16) + pos.getZ();

			int y = world.getTopY(this.heightmap, x, z);
			list.add(new BlockPos(x, y, z));
		}

		return list;
	}
}
