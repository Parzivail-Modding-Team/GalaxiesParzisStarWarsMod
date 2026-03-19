package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MutatingSlabBlock extends VerticalSlabBlock
{
	private final VerticalSlabBlock target;
	private final int meanTransitionTime;

	public MutatingSlabBlock(VerticalSlabBlock target, int meanTransitionTime, BlockBehaviour.Properties settings)
	{
		super(settings.randomTicks());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		if (!canTransition(state, world, pos, random))
			return;

		if (random.nextInt(meanTransitionTime) == 0)
			world.setBlock(pos, target.withPropertiesOf(state), Block.UPDATE_CLIENTS);
	}

	protected boolean canTransition(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		return true;
	}
}