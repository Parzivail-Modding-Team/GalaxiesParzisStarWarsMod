package com.parzivail.util.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class PBiomeGenBase extends Biome
{
	public PBiomeGenBase(Biome.BiomeProperties properties)
	{
		super(properties);
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos)
	{
	}
}
