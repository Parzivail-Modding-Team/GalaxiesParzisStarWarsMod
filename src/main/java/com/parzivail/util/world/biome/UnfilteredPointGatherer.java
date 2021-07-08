package com.parzivail.util.world.biome;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public class UnfilteredPointGatherer<T>
{
	// For handling a (jittered) hex grid
	private static final double SQRT_HALF = Math.sqrt(1.0 / 2.0);
	private static final double TRIANGLE_EDGE_LENGTH = Math.sqrt(2.0 / 3.0);
	private static final double TRIANGLE_HEIGHT = SQRT_HALF;
	private static final double INVERSE_TRIANGLE_HEIGHT = SQRT_HALF * 2;
	private static final double TRIANGLE_CIRCUMRADIUS = TRIANGLE_HEIGHT * (2.0 / 3.0);
	private static final double JITTER_AMOUNT = TRIANGLE_HEIGHT;
	public static final double MAX_GRIDSCALE_DISTANCE_TO_CLOSEST_POINT = JITTER_AMOUNT + TRIANGLE_CIRCUMRADIUS;

	// Primes for jitter hash.
	private static final int PRIME_X = 7691;
	private static final int PRIME_Z = 30869;

	// Jitter in JITTER_VECTOR_COUNT_MULTIPLIER*12 directions, symmetric about the hex grid.
	// cos(t)=sin(t+const) where const=(1/4)*2pi, and N*12 is a multiple of 4, so we can overlap arrays.
	// Repeat the first in every set of three due to how the pseudo-modulo indexer works.
	// I started out with the idea of letting JITTER_VECTOR_COUNT_MULTIPLIER_POWER be configurable,
	// but it may need bit more work to ensure there are enough bits in the selector.
	private static final int JITTER_VECTOR_COUNT_MULTIPLIER_POWER = 1;
	private static final int JITTER_VECTOR_COUNT_MULTIPLIER = 1 << JITTER_VECTOR_COUNT_MULTIPLIER_POWER;
	private static final int N_VECTORS = JITTER_VECTOR_COUNT_MULTIPLIER * 12;
	private static final int N_VECTORS_WITH_REPETITION = N_VECTORS * 4 / 3;
	private static final int VECTOR_INDEX_MASK = N_VECTORS_WITH_REPETITION - 1;
	private static final int JITTER_SINCOS_OFFSET = JITTER_VECTOR_COUNT_MULTIPLIER * 4;
	private static final double[] JITTER_SINCOS;

	static
	{
		final int sinCosArraySize = N_VECTORS_WITH_REPETITION * 5 / 4;
		final double sinCosOffsetFactor = (1.0 / JITTER_VECTOR_COUNT_MULTIPLIER);
		JITTER_SINCOS = new double[sinCosArraySize];
		for (int i = 0, j = 0; i < N_VECTORS; i++)
		{
			JITTER_SINCOS[j] = Math.sin((i + sinCosOffsetFactor) * ((2.0 * Math.PI) / N_VECTORS)) * JITTER_AMOUNT;
			j++;

			// Every time you start a new set, repeat the first entry.
			// This is because the pseudo-modulo formula,
			// which aims for an even selection over 24 values,
			// reallocates the distribution over every four entries
			// from 25%,25%,25%,25% to a,b,33%,33%, where a+b=33%.
			// The particular one used here does 0%,33%,33%,33%.
			if ((j & 3) == 1)
			{
				JITTER_SINCOS[j] = JITTER_SINCOS[j - 1];
				j++;
			}
		}
		for (int j = N_VECTORS_WITH_REPETITION; j < sinCosArraySize; j++)
		{
			JITTER_SINCOS[j] = JITTER_SINCOS[j - N_VECTORS_WITH_REPETITION];
		}
	}

	private final double frequency, inverseFrequency;
	private final LatticePoint[] pointsToSearch;

	public UnfilteredPointGatherer(double frequency, double maxPointContributionRadius)
	{
		this.frequency = frequency;
		this.inverseFrequency = 1.0 / frequency;

		// How far out in the jittered hex grid we need to look for points.
		// Assumes the jitter can go any angle, which should only very occasionally
		// cause us to search one more layer out than we need.
		double maxContributingDistance = maxPointContributionRadius * frequency
		                                 + MAX_GRIDSCALE_DISTANCE_TO_CLOSEST_POINT;
		double maxContributingDistanceSq = maxContributingDistance * maxContributingDistance;
		double latticeSearchRadius = maxContributingDistance * INVERSE_TRIANGLE_HEIGHT;

		// Start at the central point, and keep traversing bigger hexagonal layers outward.
		// Exclude almost all points which can't possibly be jittered into range.
		// The "almost" is again because we assume any jitter angle is possible,
		// when in fact we only use a small set of uniformly distributed angles.
		ArrayList<LatticePoint> pointsToSearchList = new ArrayList<>();
		pointsToSearchList.add(new LatticePoint(0, 0));
		for (int i = 1; i < latticeSearchRadius; i++)
		{
			int xsv = i;
			int zsv = 0;

			while (zsv < i)
			{
				LatticePoint point = new LatticePoint(xsv, zsv);
				if (point.xv * point.xv + point.zv * point.zv < maxContributingDistanceSq)
					pointsToSearchList.add(point);
				zsv++;
			}

			while (xsv > 0)
			{
				LatticePoint point = new LatticePoint(xsv, zsv);
				if (point.xv * point.xv + point.zv * point.zv < maxContributingDistanceSq)
					pointsToSearchList.add(point);
				xsv--;
			}

			while (xsv > -i)
			{
				LatticePoint point = new LatticePoint(xsv, zsv);
				if (point.xv * point.xv + point.zv * point.zv < maxContributingDistanceSq)
					pointsToSearchList.add(point);
				xsv--;
				zsv--;
			}

			while (zsv > -i)
			{
				LatticePoint point = new LatticePoint(xsv, zsv);
				if (point.xv * point.xv + point.zv * point.zv < maxContributingDistanceSq)
					pointsToSearchList.add(point);
				zsv--;
			}

			while (xsv < 0)
			{
				LatticePoint point = new LatticePoint(xsv, zsv);
				if (point.xv * point.xv + point.zv * point.zv < maxContributingDistanceSq)
					pointsToSearchList.add(point);
				xsv++;
			}

			while (zsv < 0)
			{
				LatticePoint point = new LatticePoint(xsv, zsv);
				if (point.xv * point.xv + point.zv * point.zv < maxContributingDistanceSq)
					pointsToSearchList.add(point);
				xsv++;
				zsv++;
			}
		}

		pointsToSearch = pointsToSearchList.toArray(new LatticePoint[0]);
	}

	public List<GatheredPoint<T>> getPoints(long seed, double x, double z)
	{
		x *= frequency;
		z *= frequency;

		// Simplex 2D Skew.
		double s = (x + z) * 0.366025403784439;
		double xs = x + s, zs = z + s;

		// Base vertex of compressed square.
		int xsb = (int)xs;
		if (xs < xsb)
			xsb -= 1;
		int zsb = (int)zs;
		if (zs < zsb)
			zsb -= 1;
		double xsi = xs - xsb, zsi = zs - zsb;

		// Find closest vertex on triangle lattice.
		double p = 2 * xsi - zsi;
		double q = 2 * zsi - xsi;
		double r = xsi + zsi;
		if (r > 1)
		{
			if (p < 0)
			{
				zsb += 1;
			}
			else if (q < 0)
			{
				xsb += 1;
			}
			else
			{
				xsb += 1;
				zsb += 1;
			}
		}
		else
		{
			if (p > 1)
			{
				xsb += 1;
			}
			else if (q > 1)
			{
				zsb += 1;
			}
		}

		// Pre-multiply for hash.
		int xsbp = xsb * PRIME_X;
		int zsbp = zsb * PRIME_Z;

		// Unskewed coordinate of the closest triangle lattice vertex.
		// Everything will be relative to this.
		double bt = (xsb + zsb) * -0.211324865405187;
		double xb = xsb + bt, zb = zsb + bt;

		// Loop through pregenerated array of all points which could be in range, relative to the closest.
		ArrayList<GatheredPoint<T>> worldPointsList = new ArrayList<>(pointsToSearch.length);
		for (int i = 0; i < pointsToSearch.length; i++)
		{
			LatticePoint point = pointsToSearch[i];

			// Prime multiplications for jitter hash
			int xsvp = xsbp + point.xsvp;
			int zsvp = zsbp + point.zsvp;

			// Compute the jitter hash
			int hash = xsvp ^ zsvp;
			hash = (((int)(seed & 0xFFFFFFFFL) ^ hash) * 668908897)
			       ^ (((int)(seed >> 32) ^ hash) * 35311);

			// Even selection within 0-24, using pseudo-modulo technique.
			int indexBase = (hash & 0x3FFFFFF) * 0x5555555;
			int index = (indexBase >> 26) & VECTOR_INDEX_MASK;
			int remainingHash = indexBase & 0x3FFFFFF; // The lower bits are still good as a normal hash.

			// Jittered point, not yet unscaled for frequency
			double scaledX = xb + point.xv + JITTER_SINCOS[index];
			double scaledZ = zb + point.zv + JITTER_SINCOS[index + JITTER_SINCOS_OFFSET];

			// Unscale the coordinate and add it to the list.
			// "Unfiltered" means that, even if the jitter took it out of range, we don't check for that.
			// It's up to the user to handle out-of-range points as if they weren't there.
			// This is so that a user can implement a more limiting check (e.g. confine to a chunk square),
			// without the added overhead of this less limiting check.
			// A possible alternate implementation of this could employ a callback function,
			// to avoid adding the points to the list in the first place.
			GatheredPoint<T> worldPoint = new GatheredPoint<T>(scaledX * inverseFrequency, scaledZ * inverseFrequency, remainingHash);
			worldPointsList.add(worldPoint);
		}

		return worldPointsList;
	}

	private static class LatticePoint
	{
		public int xsvp, zsvp;
		public double xv, zv;

		public LatticePoint(int xsv, int zsv)
		{
			this.xsvp = xsv * PRIME_X;
			this.zsvp = zsv * PRIME_Z;
			double t = (xsv + zsv) * -0.211324865405187;
			this.xv = xsv + t;
			this.zv = zsv + t;
		}
	}
}
