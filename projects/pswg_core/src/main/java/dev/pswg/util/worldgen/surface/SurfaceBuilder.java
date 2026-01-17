package dev.pswg.util.worldgen.surface;

import dev.pswg.util.worldgen.world.ChunkView;
import net.minecraft.block.BlockState;

import java.util.Random;

public interface SurfaceBuilder
{

	void build(ChunkView chunk, int x, int z, int height, Random random, BlockState defaultBlock, BlockState defaultFluid);
}
