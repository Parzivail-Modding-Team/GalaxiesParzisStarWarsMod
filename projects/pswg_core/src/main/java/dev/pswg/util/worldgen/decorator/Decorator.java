package dev.pswg.util.worldgen.decorator;

import dev.pswg.util.worldgen.world.WorldGenView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public abstract class Decorator
{
	public abstract List<BlockPos> findPositions(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos);
}
