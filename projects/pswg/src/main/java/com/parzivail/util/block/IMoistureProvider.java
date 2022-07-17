package com.parzivail.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface IMoistureProvider
{
	boolean providesMoisture(WorldView world, BlockPos blockPos, BlockState blockState);
}
