package com.parzivail.util.gen.surface;

import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;

import java.util.Random;

public interface SurfaceBuilder {

	void build(ChunkView chunk, int x, int z, int height, Random random, BlockState defaultBlock, BlockState defaultFluid);
}
