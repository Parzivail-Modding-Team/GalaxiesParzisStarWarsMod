package com.parzivail.util.gen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.util.gen.BiomeGenerator;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class GalaxiesBiomeSource extends BiomeSource
{
	public static final Codec<GalaxiesBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME)
	).apply(instance, GalaxiesBiomeSource::new));

	private final BiomeGenerator backingGen = new BiomeGenerator(10000);
	private final RegistryEntryLookup<Biome> biomes;

	protected GalaxiesBiomeSource(RegistryEntryLookup<Biome> biomes)
	{
		// TODO: implement this
		super();

		this.biomes = biomes;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec()
	{
		return CODEC;
	}

	@Override
	protected Stream<RegistryEntry<Biome>> biomeStream()
	{
		return Stream.empty();
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise)
	{
		return this.biomes.getOrThrow(this.backingGen.getBiome(x, z).backing());
	}

	public BiomeGenerator getBackingGen()
	{
		return backingGen;
	}
}
