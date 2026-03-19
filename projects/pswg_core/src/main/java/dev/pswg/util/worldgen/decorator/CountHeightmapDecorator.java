package dev.pswg.util.worldgen.decorator;

import dev.pswg.util.worldgen.world.WorldGenView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class CountHeightmapDecorator extends Decorator
{
	private final int count;
	private final Heightmap.Types heightmap;

	public CountHeightmapDecorator(int count)
	{
		this(count, Heightmap.Types.WORLD_SURFACE_WG);
	}

	public CountHeightmapDecorator(int count, Heightmap.Types heightmap)
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