package com.parzivail.swg.dimension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import java.util.List;
import java.util.Random;

public class SingleBiomeProvider extends BiomeProvider
{
	/**
	 * The biome generator object.
	 */
	private final Biome biomeGenerator;
	/**
	 * The rainfall in the world
	 */
	private final float rainfall;

	public SingleBiomeProvider(Biome biome, float rainfall)
	{
		biomeGenerator = biome;
		this.rainfall = rainfall;
	}

	/**
	 * Returns the BiomeGenBase related to the x, z position on the world.
	 */
	@Override
	public Biome getBiome(BlockPos pos)
	{
		return biomeGenerator;
	}

	@Override
	public BlockPos findBiomePosition(int x, int y, int z, List biomeHaystack, Random rand)
	{
		return biomeHaystack.contains(biomeGenerator) ? new BlockPos(x - z + rand.nextInt(z * 2 + 1), 0, y - z + rand.nextInt(z * 2 + 1)) : null;
	}

	/**
	 * checks given Chunk's Biomes against List of allowed ones
	 */
	@Override
	public boolean areBiomesViable(int x, int y, int z, List biomeHaystack)
	{
		return biomeHaystack.contains(biomeGenerator);
	}
}
