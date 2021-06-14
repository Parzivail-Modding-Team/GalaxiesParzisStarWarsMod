package com.parzivail.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;

public class ChunkGenUtil
{
	public static void setBlock(Chunk chunk, ChunkSection chunkSection, BlockPos chunkLocalPos, BlockState blockState)
	{
		if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk pc)
			pc.addLightSource(chunkLocalPos);

		chunkSection.setBlockState(chunkLocalPos.getX(), chunkLocalPos.getY(), chunkLocalPos.getZ(), blockState, false);
		chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG).trackUpdate(chunkLocalPos.getX(), chunkLocalPos.getY(), chunkLocalPos.getZ(), blockState);
		chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG).trackUpdate(chunkLocalPos.getX(), chunkLocalPos.getY(), chunkLocalPos.getZ(), blockState);

		if (!blockState.getFluidState().isEmpty())
			chunk.getFluidTickScheduler().schedule(chunkLocalPos, blockState.getFluidState().getFluid(), 0);
	}
}
