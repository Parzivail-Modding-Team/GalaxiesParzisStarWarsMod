package dev.pswg.util.worldgen.surface;

import dev.pswg.util.worldgen.world.ChunkView;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TwoStateSurfaceBuilder implements SurfaceBuilder
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
	public void build(ChunkView chunk, int x, int z, int height, Random random, BlockState defaultBlock, BlockState defaultFluid)
	{
		int genDepth = 1;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int y = height; y >= 0; y--)
		{
			pos.set(x, y, z);

			if (chunk.getBlockState(pos).is(defaultBlock.getBlock()))
			{
				if (genDepth <= surfaceDepth)
				{
					chunk.setBlockState(pos, surface);
				}
				else if (genDepth <= belowSurfaceDepth + surfaceDepth)
				{
					chunk.setBlockState(pos, belowSurface);
				}

				genDepth++;
			}
			else
			{
				genDepth = 1;
			}
		}
	}
}
