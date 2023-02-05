package com.parzivail.util.gen.biome;

import com.parzivail.util.gen.decoration.ConfiguredDecoration;
import com.parzivail.util.gen.surface.SurfaceBuilder;
import com.parzivail.util.gen.terrain.TerrainBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;

public record TerrainBiome(RegistryKey<Biome> backing, SurfaceBuilder surface, TerrainBuilder terrain, List<ConfiguredDecoration> decorations) {

}
