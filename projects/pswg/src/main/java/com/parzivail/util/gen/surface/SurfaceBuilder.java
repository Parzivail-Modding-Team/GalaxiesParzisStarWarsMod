package com.parzivail.util.gen.surface;

import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.Chunk;

public abstract class SurfaceBuilder {
	public SurfaceBuilder() {

	}

	public abstract void build(Chunk chunk, int x, int z, int height, BlockState defaultBlock, BlockState defaultFluid);
}
