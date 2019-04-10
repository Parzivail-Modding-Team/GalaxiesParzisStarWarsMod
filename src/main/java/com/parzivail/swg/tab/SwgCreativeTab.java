package com.parzivail.swg.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
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
		return Items.APPLE.getDefaultInstance();
	}
}
