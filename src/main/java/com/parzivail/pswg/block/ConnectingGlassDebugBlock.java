package com.parzivail.pswg.block;

import com.parzivail.util.block.DelegatedConnectingBlock;
import net.minecraft.block.BlockState;

public class ConnectingGlassDebugBlock extends DelegatedConnectingBlock
{
	public ConnectingGlassDebugBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected boolean shouldConnectTo(BlockState self, BlockState other)
	{
		return other.isOf(self.getBlock());
	}
}
