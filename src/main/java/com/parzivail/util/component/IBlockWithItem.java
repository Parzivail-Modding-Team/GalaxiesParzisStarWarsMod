package com.parzivail.util.component;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IBlockWithItem
{
	void registerItemModel(Item itemBlock);

	Item createItemBlock();

	Block getBlock();

	String getName();
}
