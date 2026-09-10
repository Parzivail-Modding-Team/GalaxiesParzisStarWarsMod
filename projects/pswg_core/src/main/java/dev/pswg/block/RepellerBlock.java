package dev.pswg.block;

import dev.pswg.container.GalaxiesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collections;

public class RepellerBlock extends WaterloggableRotatingBlockWithBounds
{
	// TODO: move to a repellent manager to also manage where not to naturally deposit sand via weather
	private static final int REPELLENT_RADIUS = 8;
	private static final int REPELLENT_RADIUS_SQUARED = REPELLENT_RADIUS * REPELLENT_RADIUS;

	public RepellerBlock(VoxelShape shape, Substrate requiresSubstrate, BlockBehaviour.Properties settings)
	{
		super(shape, requiresSubstrate, settings.randomTicks());
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		var targets = new ArrayList<BlockPos>();

		// TODO: make a tag for sand repellents
		for (BlockPos blockPos : BlockPos.withinManhattan(pos, REPELLENT_RADIUS, REPELLENT_RADIUS, REPELLENT_RADIUS))
			if (blockPos.getCenter().distanceTo(pos.getCenter()) < REPELLENT_RADIUS_SQUARED && level.getBlockState(blockPos).is(GalaxiesBlocks.LOOSE_DESERT_SAND))
				targets.add(blockPos.immutable());

		Collections.shuffle(targets);

		for (int i = 0; i < Math.min(5, targets.size()); i++)
		{
			var blockPos = targets.get(i);
			var layerState = level.getBlockState(blockPos);
			int layerCount = layerState.getValue(AccumulatingBlock.LAYERS);
			if (layerCount == 1)
				level.removeBlock(blockPos, false);
			else
				level.setBlockAndUpdate(blockPos, layerState.setValue(AccumulatingBlock.LAYERS, layerCount - 1));
		}
	}
}