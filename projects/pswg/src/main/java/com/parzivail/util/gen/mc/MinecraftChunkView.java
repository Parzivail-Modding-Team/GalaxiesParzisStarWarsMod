package com.parzivail.util.gen.mc;

import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public record MinecraftChunkView(Chunk chunk) implements ChunkView
{
	@Override
	public ChunkPos getChunkPos()
	{
		return chunk.getPos();
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z)
	{
		return chunk.sampleHeightmap(type, x, z);
	}

	@Override
	public void setBlockState(BlockPos pos, BlockState state)
	{
		chunk.setBlockState(pos, state, false);
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity)
	{
		chunk.setBlockEntity(blockEntity);
	}

	@Override
	public void addEntity(Entity entity)
	{
		chunk.addEntity(entity);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos)
	{
		return chunk.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		return chunk.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos)
	{
		return chunk.getFluidState(pos);
	}

	@Override
	public int getHeight()
	{
		return chunk.getHeight();
	}

	@Override
	public int getBottomY()
	{
		return chunk.getBottomY();
	}
}
