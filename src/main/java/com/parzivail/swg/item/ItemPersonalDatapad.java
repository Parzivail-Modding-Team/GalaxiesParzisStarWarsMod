package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPersonalDatapad extends PItem
{
	public ItemPersonalDatapad()
	{
		super("personalDatapad");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		player.openGui(StarWarsGalaxy.instance, Resources.GUI_PERSONAL_DATAPAD, world, 0, 0, 0);
		return stack;
	}
}
