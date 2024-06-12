package com.parzivail.util.block;

import com.mojang.serialization.MapCodec;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlockWithBounds;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class LimitedStairsBlock extends WaterloggableRotatingBlockWithBounds
{
	private static final MapCodec<LimitedStairsBlock> CODEC = createCodec(LimitedStairsBlock::new);
	private static final VoxelShape SHAPE = VoxelShapes.union(
			VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1),
			VoxelShapes.cuboid(0, 0.5, 0, 0.5, 1, 1)
	);

	public LimitedStairsBlock(Settings settings)
	{
		super(SHAPE, Substrate.NONE, settings);
	}

	@Override
	protected MapCodec<LimitedStairsBlock> getCodec()
	{
		return CODEC;
	}
}
