package com.parzivail.util.gen.surface;

import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class DefaultSurfaceBuilder extends SurfaceBuilder
{
	private final BlockState surface;
	private final BlockState belowSurface;

	public DefaultSurfaceBuilder(BlockState surface, BlockState belowSurface) {
		this.surface = surface;
		this.belowSurface = belowSurface;
	}

	@Override
	public void build(ChunkView chunk, int x, int z, int height, BlockState defaultBlock, BlockState defaultFluid)
	{
		int genDepth = 0;

		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int y = height; y >= 0; y--)
		{
			pos.set(x, y, z);

			if (chunk.getBlockState(pos).isOf(defaultBlock.getBlock())) {
				if (genDepth == 0) {
					chunk.setBlockState(pos, surface);
				} else if (genDepth < 4) {
					chunk.setBlockState(pos, belowSurface);
				}

				genDepth++;
			} else {
				genDepth = 0;
			}
		}
	}
}