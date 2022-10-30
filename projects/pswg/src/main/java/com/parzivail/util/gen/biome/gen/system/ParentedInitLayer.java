package com.parzivail.util.gen.biome.gen.system;

public interface ParentedInitLayer {
    default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> parent) {
        return () -> {
            R layerSampler = parent.make();
            return context.createSampler((x, z) -> {
                context.initSeed(x, z);
                return this.sample(context, layerSampler, x, z);
            }, layerSampler);
        };
    }

    int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z);
}