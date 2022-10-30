package com.parzivail.util.gen.biome.gen;


import com.parzivail.util.gen.biome.gen.impl.TestLayer;
import com.parzivail.util.gen.biome.gen.system.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;

public final class LayersStack
{
    private static <T extends LayerSampler, C extends LayerSampleContext<T>> List<LayerFactory<T>> build(long seed, LongFunction<C> contextProvider) {
        LayerFactory<T> factory = new TestLayer().create(contextProvider.apply(seed));

        for (int i = 0; i < 7; i++)
        {
            factory = ScaleLayer.INSTANCE.create(contextProvider.apply(20 + i), factory);
        }

        // List return for allowing 3d biome layers
        return List.of(factory);
    }

    public static List<BiomeSampler> create(long seed) {
        List<BiomeSampler> ret = new ArrayList<>();

        List<LayerFactory<CachingLayerSampler>> built = build(seed, salt -> new CachingLayerContext(25, seed, salt));

        for (int i = 0; i < 1; i++) {
            ret.add(new BiomeSampler(built.get(i).make()));
        }

        return ret;
    }
}
