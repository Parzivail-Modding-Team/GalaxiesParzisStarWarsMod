package dev.pswg.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FallingMutatingBlock extends ColoredFallingBlock
{
	private final Block target;
	private final int meanTransitionTime;

	public FallingMutatingBlock(Block target, int meanTransitionTime, Properties settings, ColorRGBA colorCode)
	{
		super(colorCode, settings.randomTicks());
		this.target = target;
		this.meanTransitionTime = meanTransitionTime;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		if (!canTransition(state, world, pos, random))
			return;

		if (random.nextInt(meanTransitionTime) == 0)
			world.setBlock(pos, target.withPropertiesOf(state), UPDATE_CLIENTS);
	}

	protected boolean canTransition(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		return true;
	}
}