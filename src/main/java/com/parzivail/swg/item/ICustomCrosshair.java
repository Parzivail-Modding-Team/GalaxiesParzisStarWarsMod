package com.parzivail.swg.item;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ICustomCrosshair
{
	void drawCrosshair(ScaledResolution sr, EntityPlayer player, ItemStack item);
}
