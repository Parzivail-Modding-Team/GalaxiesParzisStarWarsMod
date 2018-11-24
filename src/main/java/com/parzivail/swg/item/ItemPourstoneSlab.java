package com.parzivail.swg.item;

import com.parzivail.swg.registry.BlockRegister;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;

public class ItemPourstoneSlab extends ItemSlab
{
	public ItemPourstoneSlab(Block parent)
	{
		super(parent, BlockRegister.pourstoneSlab, BlockRegister.pourstoneDoubleSlab, parent == BlockRegister.pourstoneDoubleSlab);
	}
}
