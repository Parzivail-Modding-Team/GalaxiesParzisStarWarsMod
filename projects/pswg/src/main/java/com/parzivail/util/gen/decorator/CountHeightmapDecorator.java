package com.parzivail.util.gen.decorator;

import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CountHeightmapDecorator extends Decorator
{
	private final int count;
	private final Heightmap.Type heightmap;

	public CountHeightmapDecorator(int count)
	{
		this(count, Heightmap.Type.WORLD_SURFACE_WG);
	}

	public CountHeightmapDecorator(int count, Heightmap.Type heightmap)
	{
		this.count = count;
		this.heightmap = heightmap;
	}

	@Override
	public List<BlockPos> findPositions(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos)
	{
		List<BlockPos> list = new ArrayList<>();

		for (int i = 0; i < this.count; i++)
		{
			int x = random.nextInt(16) + pos.getX();
			int z = random.nextInt(16) + pos.getZ();

			int y = world.getTopY(this.heightmap, x, z);
			list.add(new BlockPos(x, y, z));
		}

		return list;
	}
}
