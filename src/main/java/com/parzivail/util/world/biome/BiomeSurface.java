package com.parzivail.util.world.biome;

import net.minecraft.block.BlockState;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public record BiomeSurface(RegistryKey<Biome> biome, int height, BlockState surface)
{
}
