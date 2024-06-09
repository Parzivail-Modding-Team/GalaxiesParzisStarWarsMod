package com.parzivail.util.block.connecting;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;

public class SelfConnectingBlock extends DelegatedConnectingBlock
{
	private static final MapCodec<SelfConnectingBlock> CODEC = createCodec(SelfConnectingBlock::new);

	public SelfConnectingBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends SelfConnectingBlock> getCodec()
	{
		return CODEC;
	}

	@Override
	protected boolean shouldConnectTo(BlockState self, BlockState other)
	{
		return other.isOf(self.getBlock());
	}
}
