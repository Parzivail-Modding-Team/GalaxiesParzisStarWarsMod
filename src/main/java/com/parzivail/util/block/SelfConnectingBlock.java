package com.parzivail.util.block;

import net.minecraft.block.BlockState;

public class SelfConnectingBlock extends DelegatedConnectingBlock
{
	public SelfConnectingBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected boolean shouldConnectTo(BlockState self, BlockState other)
	{
		return other.isOf(self.getBlock());
	}
}
