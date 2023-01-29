package com.parzivail.aurek.world;

import com.parzivail.util.gen.world.ChunkView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

class ArrayChunk implements ChunkView
{
	private final int height;
	private final int minY;
	private final int maxY;
	private final ChunkPos chunkPos;
	private final BlockState[] blocks;

	public ArrayChunk(ChunkPos pos, int minY, int maxY)
	{
		this.chunkPos = pos;
		this.minY = minY;
		this.maxY = maxY;
		this.height = maxY - minY;
		this.blocks = new BlockState[16 * 16 * height];

		for (var i = 0; i < blocks.length; i++)
			blocks[i] = Blocks.VOID_AIR.getDefaultState();
	}

	@Override
	public ChunkPos getChunkPos()
	{
		return chunkPos;
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z)
	{
		for (var i = height - 1; i >= 0; i--)
			if (!blocks[packIndex(x, i, z)].isAir())
				return i;
		return 0;
	}

	@Override
	public void setBlockState(BlockPos pos, BlockState state)
	{
		if (pos.getY() < minY || pos.getY() >= maxY)
			return;
		blocks[packIndex(pos.getX() & 15, pos.getY() - minY, pos.getZ() & 15)] = state;
	}

	private static int packIndex(int x, int y, int z)
	{
		return y * 256 + z * 16 + x;
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity)
	{
	}

	@Override
	public void addEntity(Entity entity)
	{
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos)
	{
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		if (pos.getY() < minY || pos.getY() >= maxY)
			return Blocks.VOID_AIR.getDefaultState();
		return blocks[packIndex(pos.getX() & 15, pos.getY() - minY, pos.getZ() & 15)];
	}

	@Override
	public FluidState getFluidState(BlockPos pos)
	{
		return null;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public int getBottomY()
	{
		return minY;
	}
}
