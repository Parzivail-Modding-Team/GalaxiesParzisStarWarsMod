package com.parzivail.util.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IScreenShader
{
	int requestShader(EntityPlayer player, ItemStack item);
}
