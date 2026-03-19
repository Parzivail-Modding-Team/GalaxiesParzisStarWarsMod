package dev.pswg.util.worldgen.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public interface WorldGenView
{
	BlockState getBlockState(BlockPos pos);

	int getTopY(Heightmap.Types type, int x, int z);

	void setBlockState(BlockPos pos, BlockState state);

	void addEntity(Entity entity);

	default long getSeed()
	{
		return 5000;
	}

	default boolean isAir(BlockPos pos)
	{
		return getBlockState(pos).isAir();
	}
}