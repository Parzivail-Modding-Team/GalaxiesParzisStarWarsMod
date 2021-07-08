package com.parzivail.util.world.biome;

import java.util.List;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public class ScatteredBiomeBlender
{

	private final int chunkColumnCount;
	private final int blendRadiusBoundArrayCenter;
	private final double chunkWidthMinusOne;
	private final double blendRadius, blendRadiusSq;
	private final double[] blendRadiusBound;
	private final ChunkPointGatherer<LinkedBiomeWeightMap> gatherer;

	// chunkWidth should be a power of two.
	public ScatteredBiomeBlender(double samplingFrequency, double blendRadiusPadding, int chunkWidth)
	{
		this.chunkWidthMinusOne = chunkWidth - 1;
		this.chunkColumnCount = chunkWidth * chunkWidth;
		this.blendRadius = blendRadiusPadding + getInternalMinBlendRadiusForFrequency(samplingFrequency);
		this.blendRadiusSq = blendRadius * blendRadius;
		this.gatherer = new ChunkPointGatherer<>(samplingFrequency, blendRadius, chunkWidth);

		blendRadiusBoundArrayCenter = (int)Math.ceil(blendRadius) - 1;
		blendRadiusBound = new double[blendRadiusBoundArrayCenter * 2 + 1];
		for (int i = 0; i < blendRadiusBound.length; i++)
		{
			int dx = i - blendRadiusBoundArrayCenter;
			int maxDxBeforeTruncate = Math.abs(dx) + 1;
			blendRadiusBound[i] = Math.sqrt(blendRadiusSq - maxDxBeforeTruncate);
		}
	}

	public LinkedBiomeWeightMap getBlendForChunk(long seed, int chunkBaseWorldX, int chunkBaseWorldZ, BiomeEvaluationCallback callback)
	{
		// Get the list of data points in range.
		List<GatheredPoint<LinkedBiomeWeightMap>> points = gatherer.getPointsFromChunkBase(seed, chunkBaseWorldX, chunkBaseWorldZ);

		// Evaluate and aggregate all biomes to be blended in this chunk.
		LinkedBiomeWeightMap linkedBiomeMapStartEntry = null;
		for (GatheredPoint<LinkedBiomeWeightMap> point : points)
		{

			// Get the biome for this data point from the callback.
			int biome = callback.getBiomeAt(point.getX(), point.getZ());

			// Find or create the chunk biome blend weight layer entry for this biome.
			LinkedBiomeWeightMap entry = linkedBiomeMapStartEntry;
			while (entry != null)
			{
				if (entry.getBiome() == biome)
					break;
				entry = entry.getNext();
			}
			if (entry == null)
			{
				entry = linkedBiomeMapStartEntry =
						new LinkedBiomeWeightMap(biome, linkedBiomeMapStartEntry);
			}

			point.setTag(entry);
		}

		// If there is only one biome in range here, we can skip the actual blending step.
		if (linkedBiomeMapStartEntry != null && linkedBiomeMapStartEntry.getNext() == null)
		{
			double[] weights = new double[chunkColumnCount];
			linkedBiomeMapStartEntry.setWeights(weights);
			for (int i = 0; i < chunkColumnCount; i++)
			{
				weights[i] = 1.0;
			}
			return linkedBiomeMapStartEntry;
		}

		for (LinkedBiomeWeightMap entry = linkedBiomeMapStartEntry; entry != null; entry = entry.getNext())
		{
			entry.setWeights(new double[chunkColumnCount]);
		}

		double z = chunkBaseWorldZ, x = chunkBaseWorldX;
		double xStart = x;
		double xEnd = xStart + chunkWidthMinusOne;
		for (int i = 0; i < chunkColumnCount; i++)
		{
			// Consider each data point to see if it's inside the radius for this column.
			double columnTotalWeight = 0.0;
			for (GatheredPoint<LinkedBiomeWeightMap> point : points)
			{
				double dx = x - point.getX();
				double dz = z - point.getZ();

				double distSq = dx * dx + dz * dz;

				// If it's inside the radius...
				if (distSq < blendRadiusSq)
				{

					// Relative weight = [r^2 - (x^2 + z^2)]^2
					double weight = blendRadiusSq - distSq;
					weight *= weight;

					point.getTag().getWeights()[i] += weight;
					columnTotalWeight += weight;
				}
			}

			// Normalize so all weights in a column add up to 1.
			double inverseTotalWeight = 1.0 / columnTotalWeight;
			for (LinkedBiomeWeightMap entry = linkedBiomeMapStartEntry; entry != null; entry = entry.getNext())
			{
				entry.getWeights()[i] *= inverseTotalWeight;
			}

			// A double can fully represent an int, so no precision loss to worry about here.
			if (x == xEnd)
			{
				x = xStart;
				z++;
			}
			else
				x++;
		}

		return linkedBiomeMapStartEntry;
	}

	public static double getInternalMinBlendRadiusForFrequency(double samplingFrequency)
	{
		return UnfilteredPointGatherer.MAX_GRIDSCALE_DISTANCE_TO_CLOSEST_POINT / samplingFrequency;
	}

	public double getInternalBlendRadius()
	{
		return blendRadius;
	}

	private static class BiomeEvaluation
	{
		int biome;
		double tempDzSquared;

		public BiomeEvaluation(int biome)
		{
			this.biome = biome;
		}
	}
}

