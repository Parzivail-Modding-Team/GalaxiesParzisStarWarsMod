package com.parzivail.pswg.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FernBlock;

public class SwgFernBlock extends FernBlock
{
	public SwgFernBlock(Settings settings)
	{
		super(settings);
	}

	public AbstractBlock.OffsetType getOffsetType()
	{
		return AbstractBlock.OffsetType.XZ;
	}
}
