package dev.pswg.util.worldgen.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.util.worldgen.BiomeGenerator;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

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
		return Stream.empty();
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler noise)
	{
		return this.biomes.getOrThrow(this.backingGen.getBiome(x, z).backing());
	}

	public BiomeGenerator getBackingGen()
	{
		return backingGen;
	}
}