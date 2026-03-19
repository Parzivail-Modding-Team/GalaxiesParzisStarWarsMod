package dev.pswg.util.worldgen.decoration;

import dev.pswg.util.worldgen.world.WorldGenView;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface Decoration
{
	boolean generate(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos);
}