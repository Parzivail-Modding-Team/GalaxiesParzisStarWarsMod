package com.parzivail.util.gen.biome.gen.system;

public interface MergingLayer {
   default <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> layer1, LayerFactory<R> layer2) {
      return () -> {
         R layerSampler = layer1.make();
         R layerSampler2 = layer2.make();
         return context.createSampler((x, z) -> {
            context.initSeed(x, z);
            return this.sample(context, layerSampler, layerSampler2, x, z);
         }, layerSampler, layerSampler2);
      };
   }

   int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z);
}
