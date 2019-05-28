package com.parzivail.swg.tab;

import com.parzivail.swg.Resources;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class SwgCreativeTab extends CreativeTabs
{
	public SwgCreativeTab()
	{
		super("pswg");
	}

	@Override
	public ItemStack getTabIconItem()
	{
		return new ItemStack(Block.getBlockFromName(Resources.modColon("light_vertical")));
	}
}
