package com.parzivail.util.gen.surface;

import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;

public abstract class SurfaceBuilder {
	public SurfaceBuilder() {

	}

	public abstract void build(ChunkView chunk, int x, int z, int height, BlockState defaultBlock, BlockState defaultFluid);
}
