package com.parzivail.swg.tab;

import com.parzivail.swg.registry.ItemRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class PTab extends CreativeTabs
{
	public PTab()
	{
		super("tabPswg");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return ItemRegister.rifleA280;
	}
}
