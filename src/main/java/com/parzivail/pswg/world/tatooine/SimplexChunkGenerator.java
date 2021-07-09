package com.parzivail.pswg.world.tatooine;

import com.parzivail.util.world.EmptyStructuresConfig;
import com.parzivail.util.world.biome.BackingBiomeSource;
import com.parzivail.util.world.biome.CachingBlender;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class SimplexChunkGenerator extends ChunkGenerator
{
	protected final boolean hasBedrock;
	private final CachingBlender blender;
	private final BackingBiomeSource backing;

	public SimplexChunkGenerator(BiomeSource biomeSource, boolean hasBedrock)
	{
		super(biomeSource, new EmptyStructuresConfig());
		this.hasBedrock = hasBedrock;

		if (!(this.biomeSource instanceof BackingBiomeSource))
		{
			throw new IllegalStateException("Simplex terrain biome source must implement BackingBiomeSource");
		}

		this.backing = (BackingBiomeSource)this.biomeSource;

		this.blender = new CachingBlender(0.04, 24, 16);
	}

	@Override
	public ChunkGenerator withSeed(long seed)
	{
		return this;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk)
	{
		if (hasBedrock)
			return CompletableFuture.supplyAsync(() -> {

				var chunkPos = chunk.getPos();
				var pos = new BlockPos.Mutable();

				Map<RegistryKey<Biome>, double[]> typeWeights = new HashMap<>();

				var weights = this.blender.getBlendForChunk(0, chunkPos.x * 16, chunkPos.z * 16, (x0, z0) -> this.backing.getBacking((int)x0, (int)z0));

				do
				{
					typeWeights.put(weights.getBiome(), weights.getWeights());
					weights = weights.getNext();
				}
				while (weights != null);

				for (var z = 0; z < 16; z++)
					for (var x = 0; x < 16; x++)
					{
						pos.set(x, 0, z);
						chunk.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), false);

						var height = 0.0;

						for (var pair : typeWeights.entrySet())
						{
							var biomeKey = pair.getKey();
							var biomeHeight = 1; // TODO: noiseGenerators.get(biomeKey).getNoise(x, z);
							height += biomeHeight * pair.getValue()[z * 16 + x];
						}

						pos.set(x, Math.round(height), z);
						chunk.setBlockState(pos, Blocks.STONE.getDefaultState(), false);
					}

				return chunk;
			});
		else
			return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView)
	{
		return 0;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView)
	{
		return new VerticalBlockSample(0, new BlockState[0]);
	}
}
