package com.parzivail.util.gen.biome.gen.system;

public final class CachingLayerContext implements LayerSampleContext<CachingLayerSampler> {
    private final int cacheCapacity;
    private final long worldSeed;
    private long localSeed;

    public CachingLayerContext(int cacheCapacity, long seed, long salt) {
        this.worldSeed = addSalt(seed, salt);
        this.cacheCapacity = cacheCapacity;
    }

    private static long addSalt(long seed, long salt) {
        long l = mixSeed(salt, salt);
        l = mixSeed(l, salt);
        l = mixSeed(l, salt);
        long m = mixSeed(seed, l);
        m = mixSeed(m, l);
        m = mixSeed(m, l);
        return m;
    }

    private static long mixSeed(long seed, long salt) {
        seed *= seed * 6364136223846793005L + 1442695040888963407L;
        seed += salt;
        return seed;
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator layerOperator) {
        return new CachingLayerSampler(this.cacheCapacity, layerOperator);
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler) {
        return new CachingLayerSampler(Math.min(1024, cachingLayerSampler.getCapacity() * 4), layerOperator);
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator layerOperator, CachingLayerSampler cachingLayerSampler, CachingLayerSampler cachingLayerSampler2) {
        return new CachingLayerSampler(Math.min(1024, Math.max(cachingLayerSampler.getCapacity(), cachingLayerSampler2.getCapacity()) * 4), layerOperator);
    }

    @Override
    public void initSeed(long x, long y) {
        long l = this.worldSeed;
        l = mixSeed(l, x);
        l = mixSeed(l, y);
        l = mixSeed(l, x);
        l = mixSeed(l, y);
        this.localSeed = l;
    }

    @Override
    public int nextInt(int bound) {
        int i = (int) Math.floorMod(this.localSeed >> 24, (long) bound);
        this.localSeed = mixSeed(this.localSeed, this.worldSeed);
        return i;
    }
}
