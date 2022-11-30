package com.parzivail.pswg.block;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.AccumulatingBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

import java.util.function.Function;

public class LooseSandBlock extends AccumulatingBlock
{
	// TODO: move to a repellent manager to also manage where not to naturally deposit sand via weather
	private static final int REPELLENT_RADIUS = 4;
	private static final int REPELLENT_RADIUS_SQUARED = REPELLENT_RADIUS * REPELLENT_RADIUS;

	public LooseSandBlock(AbstractBlock.Settings settings, Function<ItemPlacementContext, BlockState> fullBlockState)
	{
		super(settings.ticksRandomly(), fullBlockState);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (random.nextInt(30) == 0 && isRepellentNearby(world, pos))
		{
			int i = state.get(LAYERS);
			if (i == 1)
				world.removeBlock(pos, false);
			else
				world.setBlockState(pos, state.with(LAYERS, i - 1));
		}
	}

	private static boolean isRepellentNearby(WorldView world, BlockPos pos)
	{
		// TODO: make a tag for sand repellents
		for (BlockPos blockPos : BlockPos.iterate(pos.add(-REPELLENT_RADIUS, -REPELLENT_RADIUS, -REPELLENT_RADIUS), pos.add(REPELLENT_RADIUS, REPELLENT_RADIUS, REPELLENT_RADIUS)))
			if (blockPos.getSquaredDistance(pos) < REPELLENT_RADIUS_SQUARED && world.getBlockState(blockPos).isOf(SwgBlocks.Machine.ElectrostaticRepeller))
				return true;
		return false;
	}
}
