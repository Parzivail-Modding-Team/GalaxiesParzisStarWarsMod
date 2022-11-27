package com.parzivail.util.gen.biome.gen;

import com.parzivail.util.gen.biome.gen.system.LayerSampleContext;
import com.parzivail.util.gen.biome.gen.system.LayerSampler;
import com.parzivail.util.gen.biome.gen.system.ParentedLayer;

public enum ScaleLayer implements ParentedLayer
{
    INSTANCE;

    private int transformX(int x) {
        return x >> 1;
    }

    private int transformZ(int y) {
        return y >> 1;
    }

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int tl = parent.sample(this.transformX(x), this.transformZ(z));

        // Get last bit of the x and z
        int ax = x & 1;
        int az = z & 1;

        if (ax == 0 && az == 0) {
           return tl;
        }

        // Sample with everything except the last bit.
        // This seeds each square in a 2x2 with the same seed.
        context.initSeed(x & ~1, z & ~1);

        if (ax == 0) {
            int bl = parent.sample(this.transformX(x), this.transformZ(z + 1));
            return context.choose(tl, bl);
        }

        if (az == 0) {
            int tr = parent.sample(this.transformX(x + 1), this.transformZ(z));
            return context.choose(tl, tr);
        }

        // Perform regular sampling
        int bl = parent.sample(this.transformX(x), this.transformZ(z + 1));
        int tr = parent.sample(this.transformX(x + 1), this.transformZ(z));
        int br = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));

        return this.sample(context, tl, tr, bl, br);
    }

    // Picks the most common side, or if every side is different, picks a random one
    protected int sample(LayerSampleContext<?> context, int a, int b, int c, int d) {
        if (b == c && c == d) {
            return b;
        } else if (a == b && a == c) {
            return a;
        } else if (a == b && a == d) {
            return a;
        } else if (a == c && a == d) {
            return a;
        } else if (a == b && c != d) {
            return a;
        } else if (a == c && b != d) {
            return a;
        } else if (a == d && b != c) {
            return a;
        } else if (b == c && a != d) {
            return b;
        } else if (b == d && a != c) {
            return b;
        } else {
            return c == d && a != b ? c : context.choose(a, b, c, d);
        }
    }
}
