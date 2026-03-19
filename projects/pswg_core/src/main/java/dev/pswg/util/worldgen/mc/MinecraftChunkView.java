package dev.pswg.util.worldgen.mc;

import dev.pswg.util.worldgen.world.ChunkView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public record MinecraftChunkView(ChunkAccess chunk) implements ChunkView
{
	@Override
	public ChunkPos getChunkPos()
	{
		return chunk.getPos();
	}

	@Override
	public int sampleHeightmap(Heightmap.Types type, int x, int z)
	{
		return chunk.getHeight(type, x, z);
	}

	@Override
	public void setBlockState(BlockPos pos, BlockState state)
	{
		chunk.setBlockState(pos, state, 0);
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
	public int getMinY()
	{
		return chunk.getMinY();
	}
}