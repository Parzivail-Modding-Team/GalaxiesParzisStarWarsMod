package com.parzivail.util.gen.surface;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class DefaultSurfaceBuilder extends SurfaceBuilder
{
	private final BlockState surface;
	private final BlockState belowSurface;

	public DefaultSurfaceBuilder(BlockState surface, BlockState belowSurface) {
		this.surface = surface;
		this.belowSurface = belowSurface;
	}

	@Override
	public void build(Chunk chunk, int x, int z, int height, BlockState defaultBlock, BlockState defaultFluid)
	{
		int genDepth = 0;

		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int y = height; y >= 0; y--)
		{
			pos.set(x, y, z);

			if (chunk.getBlockState(pos).isOf(defaultBlock.getBlock())) {
				if (genDepth == 0) {
					chunk.setBlockState(pos, surface, false);
				} else if (genDepth < 4) {
					chunk.setBlockState(pos, belowSurface, false);
				}

				genDepth++;
			} else {
				genDepth = 0;
			}
		}
	}
}
