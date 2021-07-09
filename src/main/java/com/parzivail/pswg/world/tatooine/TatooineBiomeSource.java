package com.parzivail.pswg.world.tatooine;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.util.world.biome.BackingBiomeSource;
import com.parzivail.util.world.biome.BiomeEntry;
import com.parzivail.util.world.biome.SimplexBiomeSampler;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;

public class TatooineBiomeSource extends BiomeSource implements BackingBiomeSource
{
	private static final List<BiomeEntry> BIOME_VERTICES = ImmutableList.of(
			new BiomeEntry(BiomeKeys.DESERT_HILLS, 0.6f, 0.20f), // dunes
			new BiomeEntry(BiomeKeys.BADLANDS, 0.45f, 0.75f), // canyons
			new BiomeEntry(BiomeKeys.BADLANDS_PLATEAU, 0.202f, 0.867f), // plateaus
			new BiomeEntry(BiomeKeys.MUSHROOM_FIELDS, 0.9f, 0.949f), // mushroom mesas
			new BiomeEntry(BiomeKeys.BASALT_DELTAS, 0.819f, 0.72f), // wastes
			new BiomeEntry(BiomeKeys.MOUNTAINS, 0.743f, 0.515f), // mountains
			new BiomeEntry(BiomeKeys.DESERT, 0.15f, 0.325f), // salt flats
			new BiomeEntry(BiomeKeys.BEACH, 0.067f, 0.057f) // oasis
	);

	public static final Codec<TatooineBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(source -> source.biomeRegistry)
			)
			.apply(instance, instance.stable(TatooineBiomeSource::new)));

	private final SimplexBiomeSampler backingSampler;

	private final Registry<Biome> biomeRegistry;
	private final long seed;

	public TatooineBiomeSource(Registry<Biome> biomeRegistry)
	{
		super(BIOME_VERTICES.stream().map((entry) -> () -> (Biome)biomeRegistry.getOrThrow(entry.key())));
		this.biomeRegistry = biomeRegistry;
		this.seed = 0;

		this.backingSampler = new SimplexBiomeSampler(biomeRegistry, BIOME_VERTICES, seed);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		return getBiomeAt(biomeX, biomeZ);
	}

	public Biome getBiomeAt(int biomeX, int biomeZ)
	{
		RegistryKey<Biome> key = this.biomeRegistry.getKey(this.backingSampler.sample(this.biomeRegistry, biomeX << 2, biomeZ << 2)).get();

		return this.biomeRegistry.get(key);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec()
	{
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long l)
	{
		return new TatooineBiomeSource(this.biomeRegistry);
	}

	@Override
	public RegistryKey<Biome> getBacking(int x, int z)
	{
		return this.biomeRegistry.getKey(this.backingSampler.sample(this.biomeRegistry, x, z)).get();
	}
}
