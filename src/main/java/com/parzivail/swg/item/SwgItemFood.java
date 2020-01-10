package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.item.ItemFood;

public class SwgItemFood extends ItemFood
{
	public final String name;

	public SwgItemFood(String name, int food, float sat)
	{
		super(food, sat, false);
		this.name = name;
		setRegistryName(Resources.modColon(this.name));
		setUnlocalizedName(Resources.modDot(this.name));
		setCreativeTab(StarWarsGalaxy.tab);
	}
}
