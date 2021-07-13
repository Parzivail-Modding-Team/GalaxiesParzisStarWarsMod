package com.parzivail.pswg.world.feature;

import com.mojang.serialization.Codec;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.BushLeavesBlock;
import com.parzivail.util.world.ProcNoise;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class JaporTreeFeature extends Feature<SingleStateFeatureConfig>
{
	public JaporTreeFeature(Codec<SingleStateFeatureConfig> codec)
	{
		super(codec);
	}

	public boolean generate(FeatureContext<SingleStateFeatureConfig> context)
	{
		BlockPos origin = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		ProcNoise noise = new ProcNoise(random.nextLong());

		BlockState trunk = SwgBlocks.Log.Japor.getDefaultState();
		BlockState leaves = SwgBlocks.Leaves.Japor.getDefaultState().with(BushLeavesBlock.DISTANCE, 1);

		for (; origin.getY() > structureWorldAccess.getBottomY() + 3; origin = origin.down())
		{
			if (!structureWorldAccess.isAir(origin.down()))
			{
				BlockState blockState = structureWorldAccess.getBlockState(origin.down());
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
			for (int y = 0; y < height; ++y)
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
