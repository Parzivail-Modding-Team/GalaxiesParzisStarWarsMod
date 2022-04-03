package com.parzivail.pswg.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.DyeColor;

public class CreatureCageBlock extends Block
{
	public static class Item extends BlockItem
	{
		private final CreatureCageBlock block;

		public Item(CreatureCageBlock block, net.minecraft.item.Item.Settings settings)
		{
			super(block, settings);
			this.block = block;
		}
	}

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
