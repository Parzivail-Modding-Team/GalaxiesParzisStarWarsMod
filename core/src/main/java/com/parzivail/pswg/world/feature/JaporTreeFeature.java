package com.parzivail.pswg.world.feature;

import com.mojang.serialization.Codec;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.BushLeavesBlock;
import com.parzivail.util.world.ProcNoise;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class JaporTreeFeature extends Feature<SingleStateFeatureConfig>
{
	public JaporTreeFeature(Codec<SingleStateFeatureConfig> codec)
	{
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SingleStateFeatureConfig> context)
	{
		var origin = context.getOrigin();
		var structureWorldAccess = context.getWorld();
		var random = context.getRandom();
		var noise = new ProcNoise(random.nextLong());

		var trunk = SwgBlocks.Tree.JaporLog.getDefaultState();
		var leaves = SwgBlocks.Tree.JaporLeaves.getDefaultState().with(BushLeavesBlock.DISTANCE, 1);

		for (; origin.getY() > structureWorldAccess.getBottomY() + 3; origin = origin.down())
		{
			if (!structureWorldAccess.isAir(origin.down()))
			{
				var blockState = structureWorldAccess.getBlockState(origin.down());
				if (blockState.isOf(SwgBlocks.Sand.Desert))
					break;
			}
		}

		if (origin.getY() <= structureWorldAccess.getBottomY() + 3)
		{
			return false;
		}
		else
		{
			var height = random.nextInt(3) + 1;
			for (var y = 0; y < height; ++y)
			{
				var pos = origin.add(0, y, 0);
				structureWorldAccess.setBlockState(pos, trunk, Block.NO_REDRAW);

				pos = origin.add(-1, y, 0);
				structureWorldAccess.setBlockState(pos, leaves.with(BushLeavesBlock.FACING, Direction.WEST), Block.NO_REDRAW);

				pos = origin.add(1, y, 0);
				structureWorldAccess.setBlockState(pos, leaves.with(BushLeavesBlock.FACING, Direction.EAST), Block.NO_REDRAW);

				pos = origin.add(0, y, -1);
				structureWorldAccess.setBlockState(pos, leaves.with(BushLeavesBlock.FACING, Direction.NORTH), Block.NO_REDRAW);

				pos = origin.add(0, y, 1);
				structureWorldAccess.setBlockState(pos, leaves.with(BushLeavesBlock.FACING, Direction.SOUTH), Block.NO_REDRAW);
			}

			var pos = origin.add(0, height, 0);
			structureWorldAccess.setBlockState(pos, leaves.with(BushLeavesBlock.FACING, Direction.UP), Block.NO_REDRAW);

			return true;
		}
	}
}
