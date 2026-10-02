package dev.pswg.util.worldgen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.util.worldgen.BiomeGenerator;
import dev.pswg.util.worldgen.biome.BiomeList;
import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

/**
 * Supplies Tatooine biomes from PSWG's layered biome sampler.
 */
public class GalaxiesBiomeSource extends BiomeSource
{
	public static final MapCodec<GalaxiesBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryOps.retrieveGetter(Registries.BIOME)
	).apply(instance, GalaxiesBiomeSource::new));

	private final BiomeGenerator backingGen = new BiomeGenerator(10000);
	private final HolderGetter<Biome> biomes;

	protected GalaxiesBiomeSource(HolderGetter<Biome> biomes)
	{
		// TODO: implement this
		super();

		this.biomes = biomes;
	}

	@Override
	protected MapCodec<? extends BiomeSource> codec()
	{
		return CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes()
	{
		return BiomeList.stream().map(biome -> this.biomes.getOrThrow(biome.backing()));
	}

	/**
	 * Creates the resolver used to sample Tatooine's biome layers.
	 *
	 * @param sampler the cached climate sampler supplied by Minecraft
	 * @return a resolver for PSWG's biome layers
	 */
	@Override
	public BiomeResolver createResolver(Climate.Sampler sampler)
	{
		return (x, y, z) -> this.biomes.getOrThrow(this.backingGen.getBiome(x, z).backing());
	}

	public BiomeGenerator getBackingGen()
	{
		return backingGen;
	}
}
