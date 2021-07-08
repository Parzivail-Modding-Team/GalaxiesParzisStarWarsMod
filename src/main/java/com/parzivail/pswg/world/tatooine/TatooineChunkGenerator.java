package com.parzivail.pswg.world.tatooine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.util.noise.OpenSimplex2F;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class TatooineChunkGenerator extends SimplexChunkGenerator
{
	public static final Codec<TatooineChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource)
			)
			.apply(instance, instance.stable(TatooineChunkGenerator::new))
	);

	private static final OpenSimplex2F BIOME_EROSION_NOISE = new OpenSimplex2F(10);
	private static final OpenSimplex2F BIOME_DENSITY_NOISE = new OpenSimplex2F(11);

	private static final BiomeEntry[] BIOMES = new BiomeEntry[] {
			new BiomeEntry(6, 0.6f, 0.20f),
			new BiomeEntry(7, 0.45f, 0.75f),
			new BiomeEntry(5, 0.202f, 0.867f),
			new BiomeEntry(4, 0.9f, 0.949f),
			new BiomeEntry(7, 0.819f, 0.72f),
			new BiomeEntry(8, 0.743f, 0.515f),
			new BiomeEntry(3, 0.15f, 0.325f),
			new BiomeEntry(2, 0.067f, 0.057f)
	};

	public TatooineChunkGenerator(BiomeSource biomeSource)
	{
		super(biomeSource, true);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec()
	{
		return CODEC;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk)
	{
		// "decorate"
	}

	private static double octaveNoise(OpenSimplex2F source, double x, double z, int octaves)
	{
		double n = (source.noise2(x, z) + 1) / 4;
		if (octaves <= 1)
			return n / (1 - 1 / Math.pow(2, octaves));
		return n + octaveNoise(source, (x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
	}

	@Override
	protected BiomeEntry getBiome(double x, double z)
	{
		var erosion = octaveNoise(BIOME_EROSION_NOISE, x / 2048f, z / 2048f, 6);
		var density = octaveNoise(BIOME_DENSITY_NOISE, x / 2048f, z / 2048f, 6);

		var closest = BIOMES[0];
		var closestDistance = Math.pow(closest.erosionFactor() - erosion, 2) + Math.pow(closest.densityFactor() - density, 2);

		for (var i = 1; i < BIOMES.length; i++)
		{
			var d = Math.pow(BIOMES[i].erosionFactor() - erosion, 2) + Math.pow(BIOMES[i].densityFactor() - density, 2);
			if (!(d < closestDistance))
				continue;

			closestDistance = d;
			closest = BIOMES[i];
		}

		return closest;
	}
}
