package dev.pswg.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SelfConnectingBlock extends DelegatedConnectingBlock
{
	public SelfConnectingBlock(BlockBehaviour.Properties settings)
	{
		super(settings);
	}

	@Override
	protected boolean shouldConnectTo(BlockState self, BlockState other)
	{
		return other.is(self.getBlock());
	}

	@Override
	protected MapCodec<? extends PipeBlock> codec()
	{
		return simpleCodec(SelfConnectingBlock::new);
	}
}
