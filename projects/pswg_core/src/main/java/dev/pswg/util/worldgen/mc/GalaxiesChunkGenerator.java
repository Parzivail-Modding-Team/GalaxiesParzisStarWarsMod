package dev.pswg.util.worldgen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.util.worldgen.TerrainGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GalaxiesChunkGenerator extends ChunkGenerator
{
	public static final MapCodec<GalaxiesChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(GalaxiesChunkGenerator::getBiomeSource)).apply(instance, GalaxiesChunkGenerator::new));

	private final TerrainGenerator backing;

	public GalaxiesChunkGenerator(BiomeSource biomeSource)
	{
		super(biomeSource);

		if (!(biomeSource instanceof GalaxiesBiomeSource bs))
		{
			throw new IllegalStateException("Biome source must be galaxies biome source");
		}

		this.backing = new TerrainGenerator(100, bs.getBackingGen(), this, Blocks.STONE.defaultBlockState());
	}

	@Override
	public BiomeSource getBiomeSource()
	{
		return super.getBiomeSource();
	}

	@Override
	protected MapCodec<? extends ChunkGenerator> codec()
	{
		return CODEC;
	}

	@Override
	public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager biomeAccess, StructureManager structureAccessor, ChunkAccess chunk)
	{

	}

	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk)
	{
		this.backing.buildSurface(new MinecraftChunkView(chunk));
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region)
	{

	}

	@Override
	public int getGenDepth()
	{
		return -64;
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk)
	{
		this.backing.buildNoise(new MinecraftChunkView(chunk));

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public void createStructures(RegistryAccess registryManager, ChunkGeneratorStructureState placementCalculator, StructureManager structureAccessor, ChunkAccess chunk, StructureTemplateManager structureTemplateManager, ResourceKey<Level> dimension)
	{
		super.createStructures(registryManager, placementCalculator, structureAccessor, chunk, structureTemplateManager, dimension);
	}

	@Override
	public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor)
	{
		this.backing.generateDecorations(new MinecraftWorldView(world), new MinecraftChunkView(chunk));
	}

	@Override
	public int getSeaLevel()
	{
		return 0;
	}

	@Override
	public int getMinY()
	{
		return -64;
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig)
	{
		return 0;
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState noiseConfig)
	{
		return new NoiseColumn(0, new BlockState[0]);
	}

	@Override
	public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos)
	{

	}
}