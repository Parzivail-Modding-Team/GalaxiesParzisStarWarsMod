package dev.pswg.util.worldgen.biome;

import dev.pswg.util.worldgen.decoration.ConfiguredDecoration;
import dev.pswg.util.worldgen.surface.SurfaceBuilder;
import dev.pswg.util.worldgen.terrain.TerrainBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;

public record TerrainBiome(RegistryKey<Biome> backing, SurfaceBuilder surface, TerrainBuilder terrain, List<ConfiguredDecoration> decorations)
{

}