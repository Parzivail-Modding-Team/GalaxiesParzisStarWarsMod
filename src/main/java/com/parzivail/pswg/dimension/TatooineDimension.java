package com.parzivail.pswg.dimension;

import com.parzivail.pswg.container.SwgDimensions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import javax.annotation.Nullable;

public class TatooineDimension extends Dimension
{
	public TatooineDimension(World world, DimensionType type)
	{
		super(world, type, 0); // TODO: what's 0 here?
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator()
	{
		return null; // TODO
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity)
	{
		return null; // TODO
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity)
	{
		return null; // TODO
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta)
	{
		return 0; // TODO
	}

	@Override
	public boolean hasVisibleSky()
	{
		return false; // TODO
	}

	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta)
	{
		return null; // TODO
	}

	@Override
	public boolean canPlayersSleep()
	{
		return false; // TODO
	}

	@Override
	public boolean isFogThick(int x, int z)
	{
		return false; // TODO
	}

	@Override
	public DimensionType getType()
	{
		return SwgDimensions.Planets.TATOOINE;
	}
}
