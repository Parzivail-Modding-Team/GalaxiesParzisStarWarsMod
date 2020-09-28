package com.parzivail.pswg.dimension.tatooine;

//public class ChunkGeneratorTatooine extends ChunkGenerator<MultiCompositeTerrain>
//{
//	public ChunkGeneratorTatooine(WorldAccess world, BiomeSource biomeSource, MultiCompositeTerrain config)
//	{
//		super(world, biomeSource, config);
//	}
//
//	@Override
//	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
//	{
//		ChunkPos chunkPos = chunk.getPos();
//		int k = chunkPos.getStartX();
//		int l = chunkPos.getStartZ();
//		BlockPos.Mutable blockPos = new BlockPos.Mutable();
//		BlockState tatooineSand = SwgBlocks.Sand.Tatooine.getDefaultState();
//
//		for (int m = 0; m < 16; m++)
//		{
//			for (int n = 0; n < 16; n++)
//			{
//				int o = k + m;
//				int p = l + n;
//				int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n);
//				int e = Math.max(1, (int)(q * 0.15));
//				for (int y = 0; y < e; y++)
//				{
//					BlockState blockState = chunk.getBlockState(blockPos.set(o, q - y, p));
//					if (blockState.getBlock() == Blocks.STONE || (q < 2 && (q - y) != 0))
//					{
//						chunk.setBlockState(blockPos, tatooineSand, false);
//					}
//				}
//			}
//		}
//
//		ScarifChunk strChunk = SwgStructures.General.Region.openChunk(chunkPos);
//		if (strChunk != null)
//		{
//			strChunk.init();
//
//			for (Map.Entry<BlockPos, CompoundTag> tile : strChunk.tiles.entrySet())
//				chunk.addPendingBlockEntityTag(tile.getValue());
//
//			for (int i = 0; i < strChunk.numSections; i++)
//			{
//				ScarifSection section = strChunk.readSection();
//
//				for (int y = 0; y < 16; y++)
//				{
//					for (int z = 0; z < 16; z++)
//					{
//						for (int x = 0; x < 16; x++)
//						{
//							BlockState blockState = section.palette[section.blockStates[y * 256 + z * 16 + x]];
//							chunk.setBlockState(blockPos.set(k + x, section.y * 16 + y, l + z), blockState, false);
//						}
//					}
//				}
//			}
//		}
//
//		this.buildBedrock(chunk);
//	}
//
//	private void buildBedrock(Chunk chunk)
//	{
//		final BlockPos.Mutable blockPos = new BlockPos.Mutable();
//		final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
//		for (int i = 0; i < 16; i++)
//		{
//			for (int j = 0; j < 16; j++)
//			{
//				chunk.setBlockState(blockPos.set(i, 0, j), BEDROCK, false);
//			}
//		}
//	}
//
//	@Override
//	public int getSpawnHeight()
//	{
//		Chunk chunk = this.world.getChunk(0, 0);
//		return chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, 8, 8);
//	}
//
//	@Override
//	public void populateNoise(WorldAccess world, Chunk chunk)
//	{
//		MultiCompositeTerrain terrain = this.getStructuresConfig();
//
//		BlockPos.Mutable mutable = new BlockPos.Mutable();
//		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
//		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
//
//		ChunkPos chunkPos = chunk.getPos();
//
//		for (int j = 0; j < 16; ++j)
//		{
//			for (int k = 0; k < 16; ++k)
//			{
//				BlockState blockState = terrain.getDefaultBlock();
//				int height = (int)terrain.getHeightAt(chunkPos.x * 16 + j, chunkPos.z * 16 + k);
//
//				for (int i = 0; i <= height; i++)
//				{
//					chunk.setBlockState(mutable.set(j, i, k), blockState, false);
//					heightmap.trackUpdate(j, i, k, blockState);
//					heightmap2.trackUpdate(j, i, k, blockState);
//				}
//			}
//		}
//	}
//
//	@Override
//	public int getHeight(int x, int z, Heightmap.Type heightmapType)
//	{
//		return 0;
//	}
//}
