package com.parzivail.util.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FernBlock;

public class PFernBlock extends FernBlock
{
	public PFernBlock(Settings settings)
	{
		super(settings);
	}

	public AbstractBlock.OffsetType getOffsetType()
	{
		return AbstractBlock.OffsetType.XZ;
	}
}
