package com.parzivail.util.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//public class EmptyChunkGenerator extends ChunkGenerator
//{
//	public static final Codec<EmptyChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance
//			.group(
//					BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
//					Codec.BOOL.fieldOf("has_bedrock").forGetter((generator) -> generator.hasBedrock)
//			)
//			.apply(instance, instance.stable(EmptyChunkGenerator::new))
//	);
//
//	protected final boolean hasBedrock;
//
//	public EmptyChunkGenerator(BiomeSource biomeSource, boolean hasBedrock)
//	{
//		super(biomeSource, new EmptyStructuresConfig());
//		this.hasBedrock = hasBedrock;
//	}
//
//	@Override
//	protected Codec<? extends ChunkGenerator> getCodec()
//	{
//		return CODEC;
//	}
//
//	@Override
//	public ChunkGenerator withSeed(long seed)
//	{
//		return this;
//	}
//
//	@Override
//	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler()
//	{
//		return null;
//	}
//
//	@Override
//	public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep)
//	{
//
//	}
//
//	@Override
//	public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk)
//	{
//		// "decorate"
//	}
//
//	@Override
//	public void populateEntities(ChunkRegion region)
//	{
//	}
//
//	@Override
//	public int getWorldHeight()
//	{
//		return 0;
//	}
//
//	@Override
//	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk)
//	{
//		if (hasBedrock)
//			return CompletableFuture.supplyAsync(() -> {
//				var lowestSectionIdx = chunk.getSectionIndex(0);
//				var lowestSection = chunk.getSection(lowestSectionIdx);
//
//				var pos = new BlockPos.Mutable();
//
//				for (var z = 0; z < 16; z++)
//					for (var x = 0; x < 16; x++)
//					{
//						pos.set(x, 0, z);
//						chunk.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), false);
//					}
//
//				return chunk;
//			});
//		else
//			return CompletableFuture.completedFuture(chunk);
//	}
//
//	@Override
//	public int getSeaLevel()
//	{
//		return 0;
//	}
//
//	@Override
//	public int getMinimumY()
//	{
//		return 0;
//	}
//
//	@Override
//	public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView)
//	{
//		return 0;
//	}
//
//	@Override
//	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView)
//	{
//		return new VerticalBlockSample(0, new BlockState[0]);
//	}
//}
