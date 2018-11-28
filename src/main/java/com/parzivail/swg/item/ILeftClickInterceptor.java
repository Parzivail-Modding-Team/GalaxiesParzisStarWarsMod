package com.parzivail.swg.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ILeftClickInterceptor
{
	boolean onItemLeftClick(ItemStack stack, World world, EntityPlayer player);

	boolean isLeftClickRepeatable();
}
