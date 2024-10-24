package com.parzivail.util.gen.decoration;

import com.parzivail.util.gen.world.WorldGenView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public interface Decoration {
	boolean generate(WorldGenView world, ChunkGenerator generator, Random random, BlockPos pos);
}
