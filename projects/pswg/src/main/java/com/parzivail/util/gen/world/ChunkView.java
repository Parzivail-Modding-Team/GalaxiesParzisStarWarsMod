package com.parzivail.util.gen.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;

public interface ChunkView extends BlockView
{
	ChunkPos getPos();

	int sampleHeightmap(Heightmap.Type type, int x, int z);

	void setBlockState(BlockPos pos, BlockState state);

	void setBlockEntity(BlockEntity blockEntity);

	void addEntity(Entity entity);
}
