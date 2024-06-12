package com.parzivail.pswg.block;

import com.mojang.serialization.MapCodec;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.AccumulatingBlock;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithBounds;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Collections;

public class RepellerBlock extends WaterloggableRotatingBlockWithBounds
{
	// TODO: move to a repellent manager to also manage where not to naturally deposit sand via weather
	private static final int REPELLENT_RADIUS = 8;
	private static final int REPELLENT_RADIUS_SQUARED = REPELLENT_RADIUS * REPELLENT_RADIUS;

	public RepellerBlock(VoxelShape shape, Substrate requiresSubstrate, Settings settings)
	{
		super(shape, requiresSubstrate, settings.ticksRandomly());
	}

	@Override
	protected MapCodec<RepellerBlock> getCodec()
	{
		return super.getCodec();
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		var targets = new ArrayList<BlockPos>();

		// TODO: make a tag for sand repellents
		for (BlockPos blockPos : BlockPos.iterate(pos.add(-REPELLENT_RADIUS, -REPELLENT_RADIUS, -REPELLENT_RADIUS), pos.add(REPELLENT_RADIUS, REPELLENT_RADIUS, REPELLENT_RADIUS)))
			if (blockPos.getSquaredDistance(pos) < REPELLENT_RADIUS_SQUARED && world.getBlockState(blockPos).isOf(SwgBlocks.Sand.LooseDesert))
				targets.add(blockPos.toImmutable());

		Collections.shuffle(targets);

		for (int i = 0; i < Math.min(5, targets.size()); i++)
		{
			var blockPos = targets.get(i);
			var layerState = world.getBlockState(blockPos);
			int layerCount = layerState.get(AccumulatingBlock.LAYERS);
			if (layerCount == 1)
				world.removeBlock(blockPos, false);
			else
				world.setBlockState(blockPos, layerState.with(AccumulatingBlock.LAYERS, layerCount - 1));
		}
	}
}
