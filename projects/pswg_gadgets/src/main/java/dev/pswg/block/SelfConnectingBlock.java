package dev.pswg.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;

public class SelfConnectingBlock extends DelegatedConnectingBlock
{
	public SelfConnectingBlock(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Override
	protected boolean shouldConnectTo(BlockState self, BlockState other)
	{
		return other.isOf(self.getBlock());
	}

	@Override
	protected MapCodec<? extends ConnectingBlock> getCodec()
	{
		return createCodec(SelfConnectingBlock::new);
	}
}
