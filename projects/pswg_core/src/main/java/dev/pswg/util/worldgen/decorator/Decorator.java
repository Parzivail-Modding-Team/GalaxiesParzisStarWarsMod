package dev.pswg.util.worldgen.decorator;

import dev.pswg.util.worldgen.world.WorldGenView;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;

public abstract class Decorator
{
	public abstract List<BlockPos> findPositions(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos);
}
