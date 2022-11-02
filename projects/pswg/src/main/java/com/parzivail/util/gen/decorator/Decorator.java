package com.parzivail.util.gen.decorator;

import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public abstract class Decorator
{
	public abstract List<BlockPos> findPositions(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos);
}
