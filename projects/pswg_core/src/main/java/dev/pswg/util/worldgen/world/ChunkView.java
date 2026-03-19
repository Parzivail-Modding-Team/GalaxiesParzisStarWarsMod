package dev.pswg.util.worldgen.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public interface ChunkView extends BlockGetter
{
	ChunkPos getChunkPos();

	int sampleHeightmap(Heightmap.Types type, int x, int z);

	void setBlockState(BlockPos pos, BlockState state);

	void setBlockEntity(BlockEntity blockEntity);

	void addEntity(Entity entity);
}