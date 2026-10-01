package dev.pswg.util.worldgen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.util.worldgen.TerrainGenerator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
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
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jspecify.annotations.Nullable;

/**
 * Generates Tatooine terrain using PSWG's custom terrain and decoration layers.
 */
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
	public void spawnOriginalMobs(WorldGenRegion region)
	{

	}

	@Override
	public int getGenDepth()
	{
		return -64;
	}

	/**
	 * Builds custom terrain and surface layers in Minecraft's 26.3 terrain stage.
	 *
	 * @param chunk the chunk being generated
	 * @param blender the blender for old-world terrain transitions
	 * @param randomState the worldgen random state
	 * @param structureManager the structure manager
	 * @param biomeManager the biome manager
	 * @param carverBiomeRegion the optional region used for carver biome sampling
	 * @param possibleBiomes the possible biomes in the generated chunk
	 * @return the completed chunk
	 */
	@Override
	public CompletableFuture<ChunkAccess> buildTerrain(
			ChunkAccess chunk,
			Blender blender,
			RandomState randomState,
			StructureManager structureManager,
			BiomeManager biomeManager,
			@Nullable WorldGenRegion carverBiomeRegion,
			Set<Holder<Biome>> possibleBiomes)
	{
		this.backing.buildNoise(new MinecraftChunkView(chunk));
		this.backing.buildSurface(new MinecraftChunkView(chunk));

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
	public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos, SamplerContext samplerContext)
	{

	}
}
