package com.parzivail.util.gen.surface;

import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TwoStateSurfaceBuilder extends SurfaceBuilder
{
	private final BlockState surface;
	private final int surfaceDepth;
	private final BlockState belowSurface;
	private final int belowSurfaceDepth;

	public TwoStateSurfaceBuilder(BlockState surface, int surfaceDepth, BlockState belowSurface, int belowSurfaceDepth)
	{
		this.surface = surface;
		this.surfaceDepth = surfaceDepth;
		this.belowSurface = belowSurface;
		this.belowSurfaceDepth = belowSurfaceDepth;
	}

	@Override
	public void build(ChunkView chunk, int x, int z, int height, BlockState defaultBlock, BlockState defaultFluid)
	{
		int genDepth = 1;

		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int y = height; y >= 0; y--)
		{
			pos.set(x, y, z);

			if (chunk.getBlockState(pos).isOf(defaultBlock.getBlock())) {
				if (genDepth <= surfaceDepth)
				{
					chunk.setBlockState(pos, surface);
				}
				else if (genDepth <= belowSurfaceDepth + surfaceDepth)
				{
					chunk.setBlockState(pos, belowSurface);
				}

				genDepth++;
			} else {
				genDepth = 1;
			}
		}
	}
}
