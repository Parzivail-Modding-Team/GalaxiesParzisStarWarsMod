package com.parzivail.util.gen.decoration;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public abstract class Decoration {
	public abstract boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos);
}
