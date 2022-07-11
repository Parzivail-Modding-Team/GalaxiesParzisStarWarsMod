package com.parzivail.util.world.biome;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public interface BackingBiomeSource
{
	RegistryKey<Biome> getBiome(int x, int z);
}
