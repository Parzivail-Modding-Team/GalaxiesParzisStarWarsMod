package com.parzivail.util.world;

//public abstract class SimplexChunkGenerator extends ChunkGenerator
//{
//	protected final boolean hasBedrock;
//	private final long seed;
//	private final CachingBlender blender;
//	private final BackingBiomeSource backing;
//	private final ChunkRandom chunkRandom;
//
//	private final ThreadLocal<Long2ObjectLinkedOpenHashMap<BiomeSurface[]>> noiseCache = new ThreadLocal<>();
//
//	public SimplexChunkGenerator(BiomeSource biomeSource, boolean hasBedrock, long seed)
//	{
//		super(biomeSource, new EmptyStructuresConfig());
//		this.hasBedrock = hasBedrock;
//		this.seed = seed;
//
//		if (!(this.biomeSource instanceof BackingBiomeSource))
//		{
//			throw new IllegalStateException("Simplex terrain biome source must implement BackingBiomeSource");
//		}
//
//		this.backing = (BackingBiomeSource)this.biomeSource;
//		this.blender = new CachingBlender(0.04, 24, 16);
//		this.chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(seed));
//	}
//
//	@Override
//	public ChunkGenerator withSeed(long seed)
//	{
//		return this;
//	}
//
//	private BiomeSurface[] getHeightsInChunk(ChunkPos pos)
//	{
//		if (noiseCache.get() == null)
//			noiseCache.set(new Long2ObjectLinkedOpenHashMap<>());
//
//		//return cached values
//		var res = noiseCache.get().get(pos.toLong());
//		if (res != null)
//			return res;
//
//		var vals = new BiomeSurface[256];
//
//		generateNoise(vals, pos, 0, 16);
//
//		//cache the values
//		if (noiseCache.get().size() > 16)
//		{
//			noiseCache.get().removeFirst();
//		}
//
//		noiseCache.get().put(pos.toLong(), vals);
//
//		return vals;
//	}
//
//	public void generateNoise(BiomeSurface[] noise, ChunkPos pos, int start, int size)
//	{
//		for (var x = start; x < start + size; x++)
//		{
//			for (var z = 0; z < 16; z++)
//			{
//				noise[(x * 16) + z] = getSurface((pos.x * 16) + x, (pos.z * 16) + z, Heightmap.Type.WORLD_SURFACE_WG, null);
//			}
//		}
//	}
//
//	/**
//	 * Gets the "strata" block in the current column
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return Returns the strata block, or an empty Optional if it should defer to the surface hinted block
//	 */
//	protected Optional<BlockState> getStrataBlock(int x, int y, int z, BiomeSurface surface)
//	{
//		if (y == 0)
//			return Optional.of(Blocks.BEDROCK.getDefaultState());
//
//		if (y > 0.8 * surface.height())
//			return Optional.empty();
//
//		return Optional.of(Blocks.STONE.getDefaultState());
//	}
//
//	@Override
//	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk)
//	{
//		if (hasBedrock)
//			return CompletableFuture.supplyAsync(() -> {
//				var pos = new BlockPos.Mutable();
//
//				var chunkPos = chunk.getPos();
//
//				var requestedVals = getHeightsInChunk(chunk.getPos());
//
//				for (var z = 0; z < 16; z++)
//					for (var x = 0; x < 16; x++)
//					{
//						var surface = requestedVals[(x * 16) + z];
//
//						for (var y = 0; y <= surface.height(); y++)
//						{
//							var strata = getStrataBlock(chunkPos.x * 16, y, chunkPos.z * 16, surface);
//							var block = strata.orElse(surface.surface());
//
//							chunk.setBlockState(pos.set(x, y, z), block, false);
//						}
//					}
//
//				populateExtra(executor, accessor, chunk);
//
//				return chunk;
//			});
//		else
//			return CompletableFuture.completedFuture(chunk);
//	}
//
//	protected abstract void populateExtra(Executor executor, StructureAccessor accessor, Chunk chunk);
//
//	protected abstract BiomeSurfaceHint getNoise(RegistryKey<Biome> biomeKey, int x, int z);
//
//	public BiomeSurface getSurface(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView)
//	{
//		Map<RegistryKey<Biome>, double[]> typeWeights = new HashMap<>();
//
//		var weights = this.blender.getBlendForChunk(0, (x >> 4) << 4, (z >> 4) << 4, (x0, z0) -> this.backing.getBiome((int)x0, (int)z0));
//
//		do
//		{
//			typeWeights.put(weights.getBiome(), weights.getWeights());
//			weights = weights.getNext();
//		}
//		while (weights != null);
//
//		var height = 0.0;
//
//		var chunkX = x & 15;
//		var chunkZ = z & 15;
//		var weightIdx = chunkZ * 16 + chunkX;
//
//		chunkRandom.setPopulationSeed(seed, chunkX * 16, chunkZ * 16);
//		var surfaceSampler = new RandomCollection<BlockState>(chunkRandom);
//
//		for (var pair : typeWeights.entrySet())
//		{
//			var biomeKey = pair.getKey();
//			var weight = pair.getValue()[weightIdx];
//			var surfaceHint = getNoise(biomeKey, x, z);
//
//			height += surfaceHint.height() * weight;
//
//			surfaceSampler.add(Math.pow(weight, 3), surfaceHint.surface());
//		}
//
//		return new BiomeSurface(this.backing.getBiome(x, z), (int)Math.round(height), surfaceSampler.sample());
//	}
//
//	@Override
//	public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView)
//	{
//		return getSurface(x, z, heightmapType, heightLimitView).height();
//	}
//
//	@Override
//	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView)
//	{
//		return new VerticalBlockSample(0, new BlockState[0]);
//	}
//}
