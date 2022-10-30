package com.parzivail.util.gen.biome.gen.system;

public interface InitLayer {
    default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context) {
        return () -> context.createSampler((x, z) -> {
            context.initSeed(x, z);
            return this.sample(context, x, z);
        });
    }

    int sample(LayerSampleContext<?> context, int x, int z);
}