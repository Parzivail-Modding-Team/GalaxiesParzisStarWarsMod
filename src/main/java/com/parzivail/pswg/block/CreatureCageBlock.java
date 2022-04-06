package com.parzivail.pswg.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

public class CreatureCageBlock extends Block
{
	private final DyeColor color;

	public CreatureCageBlock(DyeColor color, AbstractBlock.Settings settings)
	{
		super(settings);
		this.color = color;
	}

	public DyeColor getColor()
	{
		return color;
	}
}
