package com.parzivail.util.world;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class PBiomeGenBase extends BiomeGenBase
{
	public PBiomeGenBase(int biomeId)
	{
		super(biomeId);
	}

	public void decorate(IChunkProvider provider, World world, Random rand, int worldX, int worldZ)
	{

	}
}
