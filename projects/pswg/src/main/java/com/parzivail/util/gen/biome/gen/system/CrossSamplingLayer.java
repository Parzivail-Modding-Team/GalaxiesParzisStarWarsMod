package com.parzivail.util.gen.biome.gen.system;

public interface CrossSamplingLayer extends ParentedLayer {
   int sample(LayerSampleContext<?> context, int x, int z, int n, int e, int s, int w, int center);

   default int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
      return this.sample(context, x, z,
              parent.sample(x, z - 1),
              parent.sample(x + 1, z),
              parent.sample(x, z + 1),
              parent.sample(x - 1, z),
              parent.sample(x, z));
   }
}
