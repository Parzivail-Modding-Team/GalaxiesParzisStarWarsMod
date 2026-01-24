package dev.pswg.container.worldgen;

import dev.pswg.Galaxies;
import dev.pswg.util.generic.Consumers;
import dev.pswg.util.worldgen.biome.TerrainBiomes;
import dev.pswg.util.worldgen.mc.GalaxiesBiomeSource;
import dev.pswg.util.worldgen.mc.GalaxiesChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.Consumer;

public class GalaxiesDimensions
{
	private static final Consumer<SpawnSettings.Builder> SPAWN_NONE = Consumers::noop;
	private static final Consumer<GenerationSettings.Builder> GEN_NONE = Consumers::noop;

	public static final RegistryKey<DimensionType> TATOOINE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Galaxies.id("tatooine"));

	private static int getSkyColor(float temperature)
	{
		var f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	public static void register()
	{
		Registry.register(Registries.BIOME_SOURCE, Galaxies.id("galaxies"), GalaxiesBiomeSource.CODEC);
		Registry.register(Registries.CHUNK_GENERATOR, Galaxies.id("galaxies"), GalaxiesChunkGenerator.CODEC);

		TerrainBiomes.init();
	}
}
