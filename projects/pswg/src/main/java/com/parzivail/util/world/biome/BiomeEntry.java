package com.parzivail.util.world.biome;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public record BiomeEntry(RegistryKey<Biome> key, float erosionFactor, float densityFactor)
{
}
