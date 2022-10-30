package com.parzivail.util.gen.biome.gen.system;

public interface LayerSampleContext<R extends LayerSampler> extends LayerRandomnessSource {
    void initSeed(long x, long y);

    R createSampler(LayerOperator operator);

    default R createSampler(LayerOperator operator, R parent) {
        return this.createSampler(operator);
    }

    default R createSampler(LayerOperator operator, R layerSampler, R layerSampler2) {
        return this.createSampler(operator);
    }

    default int choose(int a, int b) {
        return this.nextInt(2) == 0 ? a : b;
    }

    default int choose(int a, int b, int c, int d) {
        int choice = this.nextInt(4);
        if (choice == 0) {
            return a;
        } else if (choice == 1) {
            return b;
        } else {
            return choice == 2 ? c : d;
        }
    }
}
