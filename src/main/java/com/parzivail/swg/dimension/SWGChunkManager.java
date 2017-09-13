package com.parzivail.swg.dimension;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by colby on 9/12/2017.
 */
public class SWGChunkManager extends WorldChunkManager
{
	/**
	 * The biome generator object.
	 */
	private BiomeGenBase biomeGenerator;
	/**
	 * The rainfall in the world
	 */
	private float rainfall;

	public SWGChunkManager(BiomeGenBase biome, float rainfall)
	{
		this.biomeGenerator = biome;
		this.rainfall = rainfall;
	}

	/**
	 * Returns the BiomeGenBase related to the x, z position on the world.
	 */
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		return this.biomeGenerator;
	}

	/**
	 * Returns an array of biomes for the location input.
	 */
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int chunkX, int chunkZ, int width, int length)
	{
		if (biomes == null || biomes.length < width * length)
		{
			biomes = new BiomeGenBase[width * length];
		}

		Arrays.fill(biomes, 0, width * length, this.biomeGenerator);
		return biomes;
	}

	/**
	 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
	 */
	public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length)
	{
		if (listToReuse == null || listToReuse.length < width * length)
		{
			listToReuse = new float[width * length];
		}

		Arrays.fill(listToReuse, 0, width * length, this.rainfall);
		return listToReuse;
	}

	/**
	 * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
	 * WorldChunkManager Args: oldBiomeList, x, z, width, depth
	 */
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int length)
	{
		if (oldBiomeList == null || oldBiomeList.length < width * length)
		{
			oldBiomeList = new BiomeGenBase[width * length];
		}

		Arrays.fill(oldBiomeList, 0, width * length, this.biomeGenerator);
		return oldBiomeList;
	}

	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
	 * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
	 */
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean checkCache)
	{
		return this.loadBlockGeneratorData(listToReuse, x, z, width, length);
	}

	public ChunkPosition findBiomePosition(int x, int y, int z, List biomeHaystack, Random rand)
	{
		return biomeHaystack.contains(this.biomeGenerator) ? new ChunkPosition(x - z + rand.nextInt(z * 2 + 1), 0, y - z + rand.nextInt(z * 2 + 1)) : null;
	}

	/**
	 * checks given Chunk's Biomes against List of allowed ones
	 */
	public boolean areBiomesViable(int x, int y, int z, List biomeHaystack)
	{
		return biomeHaystack.contains(this.biomeGenerator);
	}
}