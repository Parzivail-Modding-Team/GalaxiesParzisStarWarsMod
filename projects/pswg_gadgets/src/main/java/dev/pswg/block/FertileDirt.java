package dev.pswg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class FertileDirt extends Block
{
	public FertileDirt(Settings settings)
	{
		super(settings);
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		super.randomTick(state, world, pos, random);
		var plantState = world.getBlockState(pos.up());
		if (world instanceof ServerWorld serverWorld)
			if (plantState.getBlock() instanceof Fertilizable fertilizableBlock)
				if (fertilizableBlock.canGrow(serverWorld, serverWorld.random, pos.up(), plantState))
					fertilizableBlock.grow(serverWorld, serverWorld.random, pos.up(), plantState);
	}
}
