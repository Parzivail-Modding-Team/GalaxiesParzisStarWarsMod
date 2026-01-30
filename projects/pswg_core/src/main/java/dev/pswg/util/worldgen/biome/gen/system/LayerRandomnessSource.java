package dev.pswg.util.worldgen.biome.gen.system;

public interface LayerRandomnessSource
{
	int nextInt(int bound);

	default double nextDouble()
	{
		return nextInt(Integer.MAX_VALUE) / (double)Integer.MAX_VALUE;
	}
}