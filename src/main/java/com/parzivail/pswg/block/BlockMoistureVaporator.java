package com.parzivail.pswg.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockMoistureVaporator extends RotatingBlock implements IMoistureProvider
{
	public BlockMoistureVaporator(Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean providesMoisture(WorldView world, BlockPos blockPos, BlockState blockState)
	{
		return true;
	}
}
