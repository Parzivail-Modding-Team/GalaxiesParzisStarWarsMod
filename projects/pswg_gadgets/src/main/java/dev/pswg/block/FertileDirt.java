package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FertileDirt extends Block
{
	public FertileDirt(Properties settings)
	{
		super(settings);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		super.randomTick(state, world, pos, random);
		var plantState = world.getBlockState(pos.above());
		if (world instanceof ServerLevel serverWorld)
			if (plantState.getBlock() instanceof BonemealableBlock fertilizableBlock)
				if (fertilizableBlock.isBonemealSuccess(serverWorld, serverWorld.getRandom(), pos.above(), plantState))
					fertilizableBlock.performBonemeal(serverWorld, serverWorld.getRandom(), pos.above(), plantState);
	}
}
