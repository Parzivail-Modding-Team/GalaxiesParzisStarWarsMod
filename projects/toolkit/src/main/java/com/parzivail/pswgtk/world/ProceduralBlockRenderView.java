package com.parzivail.pswgtk.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ProceduralBlockRenderView implements BlockRenderView, ChunkProvider
{
	private final LightingProvider lightingProvider;
	private final Function<BlockPos, BlockState> blockGetter;

	public ProceduralBlockRenderView(Function<BlockPos, BlockState> blockGetter)
	{
		this.blockGetter = blockGetter;
		this.lightingProvider = new LightingProvider(this, true, true);
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded)
	{
		if (!shaded)
			return 1;

		return switch (direction)
				{
					case DOWN -> 0.5f;
					case NORTH, SOUTH -> 0.8f;
					case WEST, EAST -> 0.6f;
					default -> 1;
				};
	}

	@Override
	public LightingProvider getLightingProvider()
	{
		return lightingProvider;
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver colorResolver)
	{
		return 0x22BB00;
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
		return blockGetter.apply(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos)
	{
		return Fluids.EMPTY.getDefaultState();
	}

	@Override
	public int getHeight()
	{
		return 32;
	}

	@Override
	public int getBottomY()
	{
		return 0;
	}

	@Nullable
	@Override
	public BlockView getChunk(int chunkX, int chunkZ)
	{
		// TODO
		return this;
	}

	@Override
	public BlockView getWorld()
	{
		return this;
	}
}
