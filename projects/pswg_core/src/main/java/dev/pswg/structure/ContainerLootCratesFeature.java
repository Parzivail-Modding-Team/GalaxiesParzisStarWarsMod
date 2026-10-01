package dev.pswg.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Placeholder feature retained for the container loot-crate worldgen data.
 */
public final class ContainerLootCratesFeature implements Feature
{
	/**
	 * Codec for the parameterless feature.
	 */
	private static final MapCodec<ContainerLootCratesFeature> CODEC = MapCodec.unit(new ContainerLootCratesFeature());

	/**
	 * Returns the registered feature codec.
	 *
	 * @return the feature codec
	 */
	@Override
	public MapCodec<ContainerLootCratesFeature> codec()
	{
		return CODEC;
	}

	/**
	 * Places the feature in a generated world.
	 *
	 * @param level the world being generated
	 * @param chunkGenerator the active chunk generator
	 * @param random the worldgen random source
	 * @param origin the placement origin
	 * @return whether this feature placed content
	 */
	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin)
	{
		return false;
	}
}
