package com.parzivail.util.gen.biome.gen.system;

public interface LayerFactory<A extends LayerSampler> {
    A make();
}
