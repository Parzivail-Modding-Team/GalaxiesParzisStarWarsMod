package com.parzivail.util.world.biome;

import java.util.List;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public class ChunkPointGatherer<T>
{
	private static final double CHUNK_RADIUS_RATIO = Math.sqrt(1.0 / 2.0);

	final int halfChunkWidth;
	final double maxPointContributionRadius;
	final double maxPointContributionRadiusSq;
	final double radiusPlusHalfChunkWidth;
	final UnfilteredPointGatherer<T> unfilteredPointGatherer;

	public ChunkPointGatherer(double frequency, double maxPointContributionRadius, int chunkWidth)
	{
		this.halfChunkWidth = chunkWidth / 2;
		this.maxPointContributionRadius = maxPointContributionRadius;
		this.maxPointContributionRadiusSq = maxPointContributionRadius * maxPointContributionRadius;
		this.radiusPlusHalfChunkWidth = maxPointContributionRadius + halfChunkWidth;
		unfilteredPointGatherer = new UnfilteredPointGatherer<>(frequency,
		                                                        maxPointContributionRadius + chunkWidth * CHUNK_RADIUS_RATIO);
	}

	public List<GatheredPoint<T>> getPointsFromChunkBase(long seed, int chunkBaseWorldX, int chunkBaseWorldZ)
	{
		// Technically, the true minimum is between coordinates. But tests showed it was more efficient to add before converting to doubles.
		return getPointsFromChunkCenter(seed, chunkBaseWorldX + halfChunkWidth, chunkBaseWorldZ + halfChunkWidth);
	}

	public List<GatheredPoint<T>> getPointsFromChunkCenter(long seed, int chunkCenterWorldX, int chunkCenterWorldZ)
	{
		List<GatheredPoint<T>> worldPoints =
				unfilteredPointGatherer.getPoints(seed, chunkCenterWorldX, chunkCenterWorldZ);
		for (int i = 0; i < worldPoints.size(); i++)
		{
			GatheredPoint<T> point = worldPoints.get(i);

			// Check if point contribution radius lies outside any coordinate in the chunk
			double axisCheckValueX = Math.abs(point.getX() - chunkCenterWorldX) - halfChunkWidth;
			double axisCheckValueZ = Math.abs(point.getZ() - chunkCenterWorldZ) - halfChunkWidth;
			if (axisCheckValueX >= maxPointContributionRadius || axisCheckValueZ >= maxPointContributionRadius
			    || (axisCheckValueX > 0 && axisCheckValueZ > 0
			        && axisCheckValueX * axisCheckValueX + axisCheckValueZ * axisCheckValueZ >= maxPointContributionRadiusSq))
			{

				// If so, remove it.
				// Copy the last value to this value, and remove the last,
				// to avoid shifting because order doesn't matter.
				int lastIndex = worldPoints.size() - 1;
				worldPoints.set(i, worldPoints.get(lastIndex));
				worldPoints.remove(lastIndex);
				i--;
			}
		}

		return worldPoints;
	}
}
