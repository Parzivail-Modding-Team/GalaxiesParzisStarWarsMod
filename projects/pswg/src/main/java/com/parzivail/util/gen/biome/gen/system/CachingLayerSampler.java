package com.parzivail.util.gen.biome.gen.system;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.concurrent.locks.StampedLock;

public final class CachingLayerSampler implements LayerSampler {
    private final int cacheCapacity;
    private final BiomeCache cache;

    CachingLayerSampler(int cacheCapacity, LayerOperator operator) {
        this.cache = new BiomeCache(operator, cacheCapacity);
        this.cacheCapacity = cacheCapacity;
    }

    @Override
    public int sample(int x, int z) {
        return this.cache.get(x, z);
    }

    public int getCapacity() {
        return this.cacheCapacity;
    }

    private static class BiomeCache {
        private final long[] keys;
        private final int[] values;
        private final int mask;
        private final LayerOperator operator;
        private final StampedLock lock = new StampedLock();

        private BiomeCache(LayerOperator operator, int size) {
            this.operator = operator;

            size = MathHelper.smallestEncompassingPowerOfTwo(size);
            this.mask = size - 1;

            this.keys = new long[size];
            Arrays.fill(this.keys, Long.MIN_VALUE);
            this.values = new int[size];
        }

        public int get(int x, int z) {
            long key = key(x, z);
            int idx = hash(key) & this.mask;
            long stamp = this.lock.readLock();

            // if the entry here has a key that matches ours, we have a cache hit
            if (this.keys[idx] == key) {
                int value = this.values[idx];
                this.lock.unlockRead(stamp);

                return value;
            } else {
                // cache miss: sample and put the result into our cache entry
                this.lock.unlockRead(stamp);

                stamp = this.lock.writeLock();

                int value = this.operator.apply(x, z);
                this.keys[idx] = key;
                this.values[idx] = value;

                this.lock.unlockWrite(stamp);

                return value;
            }
        }

        private int hash(long key) {
            return (int) HashCommon.mix(key);
        }

        private long key(int x, int z) {
            return ChunkPos.toLong(x, z);
        }
    }
}
