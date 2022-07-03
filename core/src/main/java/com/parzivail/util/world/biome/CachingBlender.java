package com.parzivail.util.world.biome;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.math.ChunkPos;

import java.util.Arrays;

/**
 * Author: SuperCoder79
 * Source: https://github.com/SuperCoder7979/simplexterrain
 */
public final class CachingBlender
{
	private final long[] keys;
	private final LinkedBiomeWeightMap[] values;
	private final ScatteredBiomeBlender internal;

	public CachingBlender(double samplingFrequency, double blendRadiusPadding, int chunkWidth)
	{
		this.internal = new ScatteredBiomeBlender(samplingFrequency, blendRadiusPadding, chunkWidth);

		this.keys = new long[512];
		this.values = new LinkedBiomeWeightMap[512];

		Arrays.fill(this.keys, Long.MIN_VALUE);
	}

	public LinkedBiomeWeightMap getBlendForChunk(long seed, int chunkBaseWorldX, int chunkBaseWorldZ, BiomeEvaluationCallback callback)
	{
		var key = key(chunkBaseWorldX, chunkBaseWorldZ);
		var idx = hash(key) & 511;

		if (this.keys[idx] == key)
		{
			return this.values[idx];
		}

		var weightMap = this.internal.getBlendForChunk(seed, chunkBaseWorldX, chunkBaseWorldZ, callback);
		this.values[idx] = weightMap;
		this.keys[idx] = key;

		return weightMap;
	}

	private static int hash(long key)
	{
		return (int)HashCommon.mix(key);
	}

	private static long key(int x, int z)
	{
		return ChunkPos.toLong(x, z);
	}
}
