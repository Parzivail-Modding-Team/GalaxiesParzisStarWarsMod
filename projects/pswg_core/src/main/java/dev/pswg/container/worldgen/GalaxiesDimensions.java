package dev.pswg.container.worldgen;

import dev.pswg.Galaxies;
import dev.pswg.util.generic.Consumers;
import dev.pswg.util.worldgen.biome.TerrainBiomes;
import dev.pswg.util.worldgen.mc.GalaxiesBiomeSource;
import dev.pswg.util.worldgen.mc.GalaxiesChunkGenerator;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.DimensionType;

public class GalaxiesDimensions
{
	private static final Consumer<MobSpawnSettings.Builder> SPAWN_NONE = Consumers::noop;
	private static final Consumer<BiomeGenerationSettings.PlainBuilder> GEN_NONE = Consumers::noop;

	public static final ResourceKey<DimensionType> TATOOINE = ResourceKey.create(Registries.DIMENSION_TYPE, Galaxies.id("tatooine"));

	private static int getSkyColor(float temperature)
	{
		var f = temperature / 3.0F;
		f = Mth.clamp(f, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	public static void register()
	{
		Registry.register(BuiltInRegistries.BIOME_SOURCE, Galaxies.id("galaxies"), GalaxiesBiomeSource.CODEC);
		Registry.register(BuiltInRegistries.CHUNK_GENERATOR, Galaxies.id("galaxies"), GalaxiesChunkGenerator.CODEC);

		TerrainBiomes.init();
	}
}
