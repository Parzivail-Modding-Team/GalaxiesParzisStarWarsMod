package com.parzivail.util.gen.decoration;

import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class PatchDecoration extends Decoration
{

	private final BlockState state;
	private final int radius;
	private final int size;
	private final boolean matchSurface;
	private final List<Block> targets;

	public PatchDecoration(BlockState state, int radius, int size, boolean matchSurface, List<Block> targets) {
		this.state = state;
		this.radius = radius;
		this.size = size;
		this.matchSurface = matchSurface;
		this.targets = targets;
	}

	@Override
	public boolean generate(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos)
	{

		for (int i = 0; i < size; i++)
		{
			int rx = random.nextInt(radius) - random.nextInt(radius);
			int rz = random.nextInt(radius) - random.nextInt(radius);
			int y;
			if (matchSurface) {
				y = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX() + rx, pos.getZ() + rz);
			} else {
				y = pos.getY() + random.nextInt(radius) - random.nextInt(radius);
			}

			BlockPos p = new BlockPos(pos.getX() + rx, y, pos.getZ() + rz);

			if (targets.contains(world.getBlockState(p.down()).getBlock()) && world.isAir(p))
			{
				world.setBlockState(p, state);
			}
		}

		return false;
	}
}
