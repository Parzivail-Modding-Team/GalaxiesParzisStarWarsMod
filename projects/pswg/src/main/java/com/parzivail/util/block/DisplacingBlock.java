package com.parzivail.util.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public abstract class DisplacingBlock extends WaterloggableBlock
{
	@FunctionalInterface
	public interface ShapeFunction
	{
		VoxelShape apply(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
	}

	private final ShapeFunction shapeFunction;

	public DisplacingBlock(ShapeFunction shapeFunction, Settings settings)
	{
		super(settings.dynamicBounds());
		this.shapeFunction = shapeFunction;
	}

	public DisplacingBlock(VoxelShape shape, Settings settings)
	{
		super(settings.dynamicBounds());
		this.shapeFunction = (state, world, pos, context) -> shape;
	}

	@Override
	protected abstract MapCodec<? extends DisplacingBlock> getCodec();

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return shapeFunction.apply(state, world, pos, context);
	}
}
