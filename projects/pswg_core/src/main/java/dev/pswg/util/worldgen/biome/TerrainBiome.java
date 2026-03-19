package dev.pswg.util.worldgen.biome;

import dev.pswg.util.worldgen.decoration.ConfiguredDecoration;
import dev.pswg.util.worldgen.surface.SurfaceBuilder;
import dev.pswg.util.worldgen.terrain.TerrainBuilder;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public record TerrainBiome(ResourceKey<Biome> backing, SurfaceBuilder surface, TerrainBuilder terrain, List<ConfiguredDecoration> decorations)
{

}