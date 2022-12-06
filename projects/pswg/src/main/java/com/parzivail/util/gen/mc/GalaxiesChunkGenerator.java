package com.parzivail.util.gen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.util.gen.TerrainGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GalaxiesChunkGenerator extends ChunkGenerator
{
	public static final Codec<GalaxiesChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry),
			BiomeSource.CODEC.fieldOf("biome_source").forGetter(c -> c.biomeSource)	).apply(instance, GalaxiesChunkGenerator::new));

	private final TerrainGenerator backing;

	public GalaxiesChunkGenerator(Registry<StructureSet> structureSetRegistry, BiomeSource biomeSource)
	{
		super(structureSetRegistry, Optional.empty(), biomeSource);

		if (!(biomeSource instanceof GalaxiesBiomeSource bs))
		{
			throw new IllegalStateException("Biome source must be galaxies biome source");
		}

		this.backing = new TerrainGenerator(100, bs.getBackingGen(), this, Blocks.STONE.getDefaultState());
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec()
	{
		return CODEC;
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep)
	{

	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk)
	{
		this.backing.buildSurface(new MinecraftChunkView(chunk));
	}

	@Override
	public void populateEntities(ChunkRegion region)
	{

	}

	@Override
	public int getWorldHeight()
	{
		return -64;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk)
	{
		this.backing.buildNoise(new MinecraftChunkView(chunk));

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public void setStructureStarts(DynamicRegistryManager registryManager, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager, long seed)
	{

	}

	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor)
	{
		this.backing.generateDecorations(new MinecraftWorldView(world), new MinecraftChunkView(chunk));
	}

	@Override
	public int getSeaLevel()
	{
		return 0;
	}

	@Override
	public int getMinimumY()
	{
		return -64;
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig)
	{
		return 0;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig)
	{
		return new VerticalBlockSample(0, new BlockState[0]);
	}

	@Override
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos)
	{

	}
}
