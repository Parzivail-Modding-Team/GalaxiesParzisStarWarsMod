package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class PItemFood extends ItemFood
{
	public final String name;

	public PItemFood(String name, int food, float sat)
	{
		super(food, sat, false);
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setTextureName(Resources.modColon(this.name));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return Resources.itemDot(name);
	}
}
