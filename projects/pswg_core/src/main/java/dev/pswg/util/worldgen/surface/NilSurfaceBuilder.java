package dev.pswg.util.worldgen.surface;

import dev.pswg.util.worldgen.world.ChunkView;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class NilSurfaceBuilder implements SurfaceBuilder
{
	@Override
	public void build(ChunkView chunk, int x, int z, int height, Random random, BlockState defaultBlock, BlockState defaultFluid)
	{

	}
}
