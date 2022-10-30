package com.parzivail.util.gen.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public interface WorldGenView
{
	BlockState getBlockState(BlockPos pos);

	int getTopY(Heightmap.Type type, int x, int z);

	void setBlockState(BlockPos pos, BlockState state);

	void addEntity(Entity entity);

	default long getSeed() {
		return 5000;
	}

	default boolean isAir(BlockPos pos)
	{
		return getBlockState(pos).isAir();
	}
}
