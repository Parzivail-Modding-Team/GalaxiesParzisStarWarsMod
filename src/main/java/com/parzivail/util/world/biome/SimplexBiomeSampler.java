package com.parzivail.util.world.biome;

import com.parzivail.util.noise.OpenSimplex2F;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class SimplexBiomeSampler
{
	private final OpenSimplex2F biomeErosionNoise;
	private final OpenSimplex2F biomeDensityNoise;

	private final Registry<Biome> biomeRegistry;
	private final List<BiomeEntry> vertices;

	public SimplexBiomeSampler(Registry<Biome> biomeRegistry, List<BiomeEntry> vertices, long seed)
	{
		this.biomeRegistry = biomeRegistry;
		this.vertices = vertices;

		biomeErosionNoise = new OpenSimplex2F(seed);
		biomeDensityNoise = new OpenSimplex2F(seed + 1);
	}

	private static double octaveNoise(OpenSimplex2F source, double x, double z, int octaves)
	{
		var n = (source.noise2(x, z) + 1) / 4;
		if (octaves <= 1)
			return n / (1 - 1 / Math.pow(2, octaves));
		return n + octaveNoise(source, (x + octaves * 100) * 2, (z + octaves * 100) * 2, octaves - 1) / 2;
	}

	protected BiomeEntry getBiome(double x, double z)
	{
		var erosion = octaveNoise(biomeErosionNoise, x / 2048f, z / 2048f, 6);
		var density = octaveNoise(biomeDensityNoise, x / 2048f, z / 2048f, 6);

		var closest = vertices.get(0);
		var closestDistance = Math.pow(closest.erosionFactor() - erosion, 2) + Math.pow(closest.densityFactor() - density, 2);

		for (var i = 1; i < vertices.size(); i++)
		{
			var biome = vertices.get(i);

			var d = Math.pow(biome.erosionFactor() - erosion, 2) + Math.pow(biome.densityFactor() - density, 2);
			if (!(d < closestDistance))
				continue;

			closestDistance = d;
			closest = biome;
		}

		return closest;
	}

	public Biome sample(Registry<Biome> biomeRegistry, int blockX, int blockZ)
	{
		var biomeEntry = getBiome(blockX, blockZ);
		return biomeRegistry.getOrThrow(biomeEntry.key());
	}
}
