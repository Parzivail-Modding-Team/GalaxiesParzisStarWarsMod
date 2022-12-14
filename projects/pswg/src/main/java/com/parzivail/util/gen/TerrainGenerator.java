package com.parzivail.util.gen;

import com.parzivail.util.gen.biome.TerrainBiome;
import com.parzivail.util.gen.decoration.ConfiguredDecoration;
import com.parzivail.util.gen.mc.MinecraftChunkView;
import com.parzivail.util.gen.terrain.TerrainBuilder;
import com.parzivail.util.gen.world.ChunkView;
import com.parzivail.util.gen.world.WorldGenView;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public final class TerrainGenerator
{
	private final BiomeGenerator biomes;
	private final ChunkGenerator cg;
	private final BlockState geology;

	public TerrainGenerator(long seed, BiomeGenerator biomes, ChunkGenerator cg, BlockState geology)
	{
		this.biomes = biomes;
		this.cg = cg;
		this.geology = geology;
	}

	public void buildNoise(ChunkView chunk)
	{
		ChunkPos pos = chunk.getChunkPos();
		double[][] noises = new double[25][49];
		sampleNoises(noises, pos.x << 2, pos.z << 2);

		if (chunk instanceof MinecraftChunkView mc) {
			buildMinecraftChunk(mc, noises);
		} else {
			buildGenericChunk(chunk, noises);
		}
	}

	private void buildMinecraftChunk(MinecraftChunkView chunk, double[][] noises)
	{
		Heightmap floor = chunk.chunk().getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap surface = chunk.chunk().getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		for (int nX = 0; nX < 4; nX++)
		{
			for (int nZ = 0; nZ < 4; nZ++)
			{
				for (int nY = 0; nY < 48; nY++)
				{
					ChunkSection section = chunk.chunk().getSectionArray()[nY >> 1];

					double x0y0z0 = noises[getNoiseIndex(nX, nZ)][nY];
					double x1y0z0 = noises[getNoiseIndex(nX + 1, nZ)][nY];
					double x0y0z1 = noises[getNoiseIndex(nX, nZ + 1)][nY];
					double x1y0z1 = noises[getNoiseIndex(nX + 1, nZ + 1)][nY];

					double x0y1z0 = noises[getNoiseIndex(nX, nZ)][nY + 1];
					double x1y1z0 = noises[getNoiseIndex(nX + 1, nZ)][nY + 1];
					double x0y1z1 = noises[getNoiseIndex(nX, nZ + 1)][nY + 1];
					double x1y1z1 = noises[getNoiseIndex(nX + 1, nZ + 1)][nY + 1];


					for (int pY = 0; pY < 8; pY++)
					{
						double yP = pY / 8.0;
						int rY = (nY * 8 + pY) - 64;

						double x0z0 = MathHelper.lerp(yP, x0y0z0, x0y1z0);
						double x1z0 = MathHelper.lerp(yP, x1y0z0, x1y1z0);
						double x0z1 = MathHelper.lerp(yP, x0y0z1, x0y1z1);
						double x1z1 = MathHelper.lerp(yP, x1y0z1, x1y1z1);

						for (int pX = 0; pX < 4; pX++)
						{
							double xP = pX / 4.0;
							int rX = nX * 4 + pX;

							double z0 = MathHelper.lerp(xP, x0z0, x1z0);
							double z1 = MathHelper.lerp(xP, x0z1, x1z1);

							for (int pZ = 0; pZ < 4; pZ++)
							{
								double zP = pZ / 4.0;
								int rZ = nZ * 4 + pZ;

								double noise = MathHelper.lerp(zP, z0, z1);

								BlockState state = noise > 0 ? geology : Blocks.AIR.getDefaultState();

								floor.trackUpdate(rX, rY, rZ, state);
								surface.trackUpdate(rX, rY, rZ, state);

								section.setBlockState(rX, rY & 15, rZ, state, false);
							}
						}
					}
				}
			}
		}
	}

	private void buildGenericChunk(ChunkView chunk, double[][] noises)
	{
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int nX = 0; nX < 4; nX++)
		{
			for (int nZ = 0; nZ < 4; nZ++)
			{
				for (int nY = 0; nY < 48; nY++)
				{
					double x0y0z0 = noises[getNoiseIndex(nX, nZ)][nY];
					double x1y0z0 = noises[getNoiseIndex(nX + 1, nZ)][nY];
					double x0y0z1 = noises[getNoiseIndex(nX, nZ + 1)][nY];
					double x1y0z1 = noises[getNoiseIndex(nX + 1, nZ + 1)][nY];

					double x0y1z0 = noises[getNoiseIndex(nX, nZ)][nY + 1];
					double x1y1z0 = noises[getNoiseIndex(nX + 1, nZ)][nY + 1];
					double x0y1z1 = noises[getNoiseIndex(nX, nZ + 1)][nY + 1];
					double x1y1z1 = noises[getNoiseIndex(nX + 1, nZ + 1)][nY + 1];


					for (int pY = 0; pY < 8; pY++)
					{
						double yP = pY / 8.0;
						int rY = (nY * 8 + pY) - 64;

						double x0z0 = MathHelper.lerp(yP, x0y0z0, x0y1z0);
						double x1z0 = MathHelper.lerp(yP, x1y0z0, x1y1z0);
						double x0z1 = MathHelper.lerp(yP, x0y0z1, x0y1z1);
						double x1z1 = MathHelper.lerp(yP, x1y0z1, x1y1z1);

						for (int pX = 0; pX < 4; pX++)
						{
							double xP = pX / 4.0;
							int rX = nX * 4 + pX;

							double z0 = MathHelper.lerp(xP, x0z0, x1z0);
							double z1 = MathHelper.lerp(xP, x0z1, x1z1);

							for (int pZ = 0; pZ < 4; pZ++)
							{
								double zP = pZ / 4.0;
								int rZ = nZ * 4 + pZ;

								double noise = MathHelper.lerp(zP, z0, z1);

								pos.set(rX, rY, rZ);
								chunk.setBlockState(pos, noise > 0 ? geology : Blocks.AIR.getDefaultState());
							}
						}
					}
				}
			}
		}
	}

	private static int getNoiseIndex(int x, int z)
	{
		return (x * 5) + z;
	}

	private void sampleNoises(double[][] noises, int x, int z)
	{
		for (int x1 = 0; x1 < 5; x1++)
		{
			for (int z1 = 0; z1 < 5; z1++)
			{
				Reference2DoubleMap<TerrainBuilder> builders = new Reference2DoubleOpenHashMap<>();

				for (int x2 = -2; x2 <= 2; x2++)
				{
					for (int z2 = -2; z2 <= 2; z2++)
					{
						TerrainBiome biome = biomes.getBiome(x + x1 + x2, z + z1 + z2);

						builders.mergeDouble(biome.terrain(), 1.0, Double::sum);
					}
				}

				for (Reference2DoubleMap.Entry<TerrainBuilder> e : builders.reference2DoubleEntrySet())
				{
					double weight = e.getDoubleValue() / 25.0;
					for (int i = 0; i < 49; i++)
					{
						noises[x1 * 5 + z1][i] += e.getKey().build(x + x1, i - 8, z + z1) * weight;
					}
				}
			}
		}
	}

	public void buildSurface(ChunkView chunk)
	{
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				// TODO: biome interpolator class
				TerrainBiome biome = biomes.getBiome(chunk.getChunkPos().x * 4 + (x >> 2), chunk.getChunkPos().z * 4 + (z >> 2));
				int height = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, x, z);
				biome.surface().build(chunk, x, z, height, geology, Blocks.WATER.getDefaultState());
			}
		}
	}

	public void generateDecorations(WorldGenView world, ChunkView chunk) {
		TerrainBiome biome = biomes.getBiome(chunk.getChunkPos().x * 4 + 2, chunk.getChunkPos().z * 4 + 2);

		Random random = new Random();
		long popSeed = setPopulationSeed(random, world.getSeed(), chunk.getChunkPos().x, chunk.getChunkPos().z);
		int i = 0;
		for (ConfiguredDecoration decoration : biome.decorations())
		{
			setDecoratorSeed(random, popSeed, 1, ++i);

			decoration.generate(world, this.cg, random, chunk.getChunkPos().getStartPos());
		}
	}

	public long setPopulationSeed(Random random, long worldSeed, int blockX, int blockZ) {
		random.setSeed(worldSeed);
		long l = random.nextLong() | 1L;
		long m = random.nextLong() | 1L;
		long n = (long)blockX * l + (long)blockZ * m ^ worldSeed;
		random.setSeed(n);
		return n;
	}

	public void setDecoratorSeed(Random random, long populationSeed, int index, int step) {
		long l = populationSeed + (long)index + (long)(10000 * step);
		random.setSeed(l);
	}
}
