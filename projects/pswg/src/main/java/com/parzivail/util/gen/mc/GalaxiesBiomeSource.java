package com.parzivail.util.gen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.util.gen.BiomeGenerator;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.ArrayList;

public class GalaxiesBiomeSource extends BiomeSource
{
	public static final Codec<GalaxiesBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(b -> b.biomes)
	).apply(instance, GalaxiesBiomeSource::new));

	private final BiomeGenerator backingGen = new BiomeGenerator(10000);
	private final Registry<Biome> biomes;

	protected GalaxiesBiomeSource(Registry<Biome> biomes)
	{
		// TODO: implement this
		super(new ArrayList<>());

		this.biomes = biomes;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec()
	{
		return CODEC;
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise)
	{
		return this.biomes.getEntry(this.backingGen.getBiome(x, z).backing()).orElseThrow();
	}

	public BiomeGenerator getBackingGen()
	{
		return backingGen;
	}
}
