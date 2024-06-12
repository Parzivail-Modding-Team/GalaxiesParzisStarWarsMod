package com.parzivail.util.registry;

import com.parzivail.util.block.VerticalSlabBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;

public class StoneProducts
{
	public final Block block;
	public final StairsBlock stairs;
	public final VerticalSlabBlock slab;
	public final WallBlock wall;

	public StoneProducts(Block block)
	{
		this.block = block;
		this.stairs = new StairsBlock(block.getDefaultState(), AbstractBlock.Settings.copy(block));
		this.slab = new VerticalSlabBlock(AbstractBlock.Settings.copy(block));
		this.wall = new WallBlock(AbstractBlock.Settings.copy(block));
	}
}
