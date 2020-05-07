package com.parzivail.pswg.dimension.tatooine;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.world.MultiCompositeTerrain;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkGeneratorTatooine extends ChunkGenerator<MultiCompositeTerrain>
{
	public ChunkGeneratorTatooine(IWorld world, BiomeSource biomeSource, MultiCompositeTerrain config)
	{
		super(world, biomeSource, config);
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
	{
		ChunkPos chunkPos = chunk.getPos();
		int k = chunkPos.getStartX();
		int l = chunkPos.getStartZ();
		BlockPos.Mutable blockPos = new BlockPos.Mutable();
		BlockState tatooineSand = SwgBlocks.Sand.Tatooine.getDefaultState();

		for (int m = 0; m < 16; m++)
		{
			for (int n = 0; n < 16; n++)
			{
				int o = k + m;
				int p = l + n;
				int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n);
				int e = Math.max(1, (int)(q * 0.15));
				for (int y = 0; y < e; y++)
				{
					BlockState blockState = chunk.getBlockState(blockPos.set(o, q - y, p));
					if (blockState.getBlock() == Blocks.STONE || (q < 2 && (q - y) != 0))
					{
						chunk.setBlockState(blockPos, tatooineSand, false);
					}
				}
			}
		}

		this.buildBedrock(chunk);
	}

	private void buildBedrock(Chunk chunk)
	{
		final BlockPos.Mutable blockPos = new BlockPos.Mutable();
		final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				chunk.setBlockState(blockPos.set(i, 0, j), BEDROCK, false);
			}
		}
	}

	@Override
	public int getSpawnHeight()
	{
		Chunk chunk = this.world.getChunk(0, 0);
		return chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, 8, 8);
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk)
	{
		MultiCompositeTerrain terrain = this.getConfig();

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

		ChunkPos chunkPos = chunk.getPos();

		for (int j = 0; j < 16; ++j)
		{
			for (int k = 0; k < 16; ++k)
			{
				BlockState blockState = terrain.getDefaultBlock();
				int height = (int)terrain.getHeightAt(chunkPos.x * 16 + j, chunkPos.z * 16 + k);

				for (int i = 0; i <= height; i++)
				{
					chunk.setBlockState(mutable.set(j, i, k), blockState, false);
					heightmap.trackUpdate(j, i, k, blockState);
					heightmap2.trackUpdate(j, i, k, blockState);
				}
			}
		}
	}

	@Override
	public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType)
	{
		return 0;
	}
}
