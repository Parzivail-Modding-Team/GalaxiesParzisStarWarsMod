package dev.pswg.util.worldgen.biome.gen.system;

public interface LayerFactory<A extends LayerSampler>
{
	A make();
}
